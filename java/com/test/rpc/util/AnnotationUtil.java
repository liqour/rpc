package com.test.rpc.util;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnnotationUtil {

	/**
	 * 获得包下面的所有的class
	 * 
	 * @param pack
	 *            package完整名称
	 * @return List包含所有class的实例
	 */
	public static List<Class<?>> getClasssFromPackage(String pack) {
		List<Class<?>> clazzs = new ArrayList<Class<?>>();

		// 是否循环搜索子包
		boolean recursive = true;

		// 包名字
		String packageName = pack;
		// 包名对应的路径名称
		String packageDirName = packageName.replace('.', '/');

		Enumeration<URL> dirs;

		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();

				String protocol = url.getProtocol();

				if ("file".equals(protocol)) {
					// System.out.println("file类型的扫描");
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findClassInPackageByFile(packageName, filePath, recursive, clazzs);
				} else if ("jar".equals(protocol)) {
					// System.out.println("jar类型的扫描");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return clazzs;
	}

	/**
	 * 在package对应的路径下找到所有的class
	 * 
	 * @param packageName
	 *            package名称
	 * @param filePath
	 *            package对应的路径
	 * @param recursive
	 *            是否查找子package
	 * @param clazzs
	 *            找到class以后存放的集合
	 */
	public static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive,
			List<Class<?>> clazzs) {
		File dir = new File(filePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 在给定的目录下找到所有的文件，并且进行条件过滤
		File[] dirFiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
				boolean acceptClass = file.getName().endsWith("class");// 接受class文件
				return acceptDir || acceptClass;
			}
		});

		for (File file : dirFiles) {
			if (file.isDirectory()) {
				findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);
			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Set<Class<?>> getAnnotation(String pack,Class<? extends Annotation> annotation) {
		Set<Class<?>> clazzs = new HashSet<Class<?>>();
		for(Class<?> clazz:getClasssFromPackage(pack)){
			if(null!=clazz && clazz.getAnnotation(annotation)!=null){
				clazzs.add(clazz);
			}
//			Class<?> obj = Class.forName(clazz.getName());// 静态加载类
//			boolean isEmpty = stu.isAnnotationPresent(annotation.class);// 判断stu是不是使用了我们刚才定义的注解接口if(isEmpty){
//			annotation = stu.getAnnotations();// 获取注解接口中的
//			for (Annotation a : annotation) {
//				Annotation_my my = (Annotation_my) a;// 强制转换成Annotation_my类型
//				System.out.println(stu + ":\n" + my.name() + " say: " + my.say() + " my age: " + my.age());
//			}
		}
		return clazzs;
	}
	
//	Method[] method = stu.getMethods();//
//	System.out.println("Method");for(
//	Method m:method)
//	{
//		boolean ismEmpty = m.isAnnotationPresent(com.java.annotation.Annotation_my.class);
//		if (ismEmpty) {
//			Annotation[] aa = m.getAnnotations();
//			for (Annotation a : aa) {
//				Annotation_my an = (Annotation_my) a;
//				System.out.println(m + ":\n" + an.name() + " say: " + an.say() + " my age: " + an.age());
//			}
//		}
//	}
//	// get Fields by force
//	System.out.println("get Fileds by force !");
//	Field[] field = stu.getDeclaredFields();for(
//	Field f:field)
//	{
//		f.setAccessible(true);
//		System.out.println(f.getName());
//	}System.out.println("get methods in interfaces !");
//	Class<?> interfaces[] = stu.getInterfaces();for(
//	Class<?> c:interfaces)
//	{
//		Method[] imethod = c.getMethods();
//		for (Method m : imethod) {
//			System.out.println(m.getName());
//		}
//	}
//}

}
