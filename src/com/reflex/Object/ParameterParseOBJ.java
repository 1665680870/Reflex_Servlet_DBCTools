package com.reflex.Object;

import java.lang.reflect.Type;

public class ParameterParseOBJ {
	public ParameterParseOBJ(){}
	
	public Object init(Class<?> type,Object object){
		if(object==null){
			if(type.isArray()){
				return startParse1(type, object);
			}else{
				return startParse(type, object);
			}
		}
		return object;
	}
	
	public boolean isContain(Type type){
		if(type.equals(Integer.class))return true;
		else if(type.equals(String.class))return true;
		else if(type.equals(Boolean.class))return true;
		else if(type.equals(int.class))return true;
		else if(type.equals(boolean.class))return true;
		else if(type.equals(Double.class))return true;
		else if(type.equals(double.class))return true;
		else if(type.equals(long.class))return true;
		else if(type.equals(Long.class))return true;
		else if(type.equals(Character.class))return true;
		else if(type.equals(char.class))return true;
		else if(type.equals(Byte.class))return true;
		else if(type.equals(byte.class))return true;
		else if(type.equals(Short.class))return true;
		else if(type.equals(short.class))return true;
		else if(type.equals(Float.class))return true;
		else if(type.equals(float.class))return true;
		else if(type.equals(Integer[].class))return true;
		else if(type.equals(int[].class))return true;
		else if(type.equals(Double[].class))return true;
		else if(type.equals(double[].class))return true;
		else if(type.equals(Long[].class))return true;
		else if(type.equals(long[].class))return true;
		else if(type.equals(Boolean[].class))return true;
		else if(type.equals(boolean[].class))return true;
		else if(type.equals(short[].class))return true;
		else if(type.equals(Short[].class))return true;
		else if(type.equals(Byte[].class))return true;
		else if(type.equals(byte[].class))return true;
		else if(type.equals(Float[].class))return true;
		else if(type.equals(float[].class))return true;
		else if(type.equals(Character[].class))return true;
		else if(type.equals(char[].class))return true;
		else return false;
	}
	
	public Object StringIsNull(Object object){
		if(object==null)return "";
		return object;
	}
	
	public Object startParse(Type type,Object object) {
		if (type.equals(Integer.class)) {
			return Integer.parseInt(numberAutoValue(object).toString());
		}else if (type.equals(Double.class)) {
			return Double.parseDouble(numberAutoValue(object).toString());
		}else if (type.equals(String.class)) {
			return StringIsNull(object);
		}else if (type.equals(Float.class)) {
			return Float.parseFloat(numberAutoValue(object).toString());
		}else if (type.equals(Byte.class)) {
			return Byte.parseByte(numberAutoValue(object).toString());
		}else if (type.equals(Short.class)) {
			return Short.parseShort(numberAutoValue(object).toString());
		}else if (type.equals(Long.class)) {
			return Long.parseLong(numberAutoValue(object).toString());
		}else if (type.equals(Character.class)) {
			return (Character)numberAutoValue(object).toString().charAt(0);
		}else if (type.equals(Boolean.class)){
			return Boolean.parseBoolean(booleanAutoValue(object).toString());
		}else if (type.equals(int.class)) {
			return (int)Integer.parseInt(numberAutoValue(object).toString());
		}else if (type.equals(double.class)) {
			return (double)Double.parseDouble(numberAutoValue(object).toString());
		}else if (type.equals(float.class)) {
			return (float)Float.parseFloat(numberAutoValue(object).toString());
		}else if (type.equals(byte.class)) {
			return (byte)Byte.parseByte(numberAutoValue(object).toString());
		}else if (type.equals(short.class)) {
			return (short)Short.parseShort(numberAutoValue(object).toString());
		}else if (type.equals(long.class)) {
			return (long)Long.parseLong(numberAutoValue(object).toString());
		}else if (type.equals(char.class)) {
			return numberAutoValue(object).toString().charAt(0);
		}else if (type.equals(boolean.class)) {
			return (boolean)Boolean.parseBoolean(booleanAutoValue(object).toString());
		}
		
		
		
		
		else{
			System.err.println("\n***************请填写基本类型！***************\n");
			return null;
		}
	}
	
	public Object startParseS(Type type,Object object) {
		return ArrayStringToObject(type, ArrayParseToString(object));
	}
	public Object startParse1(Type type,Object object) {
		return StringToObject(type, object);
	}
	
	public Object startParseS1(Type type,Object object) {
		if (type.equals(Integer[].class)) {
			return new Integer[]{Integer.parseInt(numberAutoValue(object).toString())};
		}else if (type.equals(Double[].class)) {
			return new Double[]{ Double.parseDouble(numberAutoValue(object).toString())};
		}else if (type.equals(String[].class)) {
			return new String[]{ StringIsNull(object).toString()};
		}else if (type.equals(Float[].class)) {
			return new Float[]{ Float.parseFloat(numberAutoValue(object).toString())};
		}else if (type.equals(Byte[].class)) {
			return new Byte[]{ Byte.parseByte(numberAutoValue(object).toString())};
		}else if (type.equals(Short[].class)) {
			return new Short[]{ Short.parseShort(numberAutoValue(object).toString())};
		}else if (type.equals(Long[].class)) {
			return new Long[]{ Long.parseLong(numberAutoValue(object).toString())};
		}else if (type.equals(Character[].class)) {
			return new Character[]{ (Character)numberAutoValue(object).toString().charAt(0)};
		}else if (type.equals(Boolean[].class)){
			return new Boolean[]{ Boolean.parseBoolean(booleanAutoValue(object).toString())};
		}else if (type.equals(int[].class)) {
			return new int[]{(int)Integer.parseInt(numberAutoValue(object).toString())};
		}else if (type.equals(double[].class)) {
			return new double[] {(double)Double.parseDouble(numberAutoValue(object).toString())};
		}else if (type.equals(float[].class)) {
			return new float[]{ (float)Float.parseFloat(numberAutoValue(object).toString())};
		}else if (type.equals(byte[].class)) {
			return new byte[]{ (byte)Byte.parseByte(numberAutoValue(object).toString())};
		}else if (type.equals(short[].class)) {
			return new short[]{ (short)Short.parseShort(numberAutoValue(object).toString())};
		}else if (type.equals(long[].class)) {
			return new long[]{ (long)Long.parseLong(numberAutoValue(object).toString())};
		}else if (type.equals(char[].class)) {
			return new char[]{ numberAutoValue(object).toString().charAt(0)};
		}else if (type.equals(boolean[].class)) {
			return new boolean[]{ (boolean)Boolean.parseBoolean(booleanAutoValue(object).toString())};
		}
		
		
		
		else{
			System.err.println("\n***************请填写基本类型！***************\n");
			return null;
		}
	}
	
	
	public String[] ArrayParseToString(Object object) {
		Type objtype=object.getClass();
		
			if (objtype.equals(Integer[].class)) {
				Integer[] objV=(Integer[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i].toString();
				}
				return values;
			}else if (objtype.equals(Double[].class)) {
				Double[] objV=(Double[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i].toString();
				}
				return values;
			}else if (objtype.equals(String[].class)) {				
				return (String[])object;
			}else if (objtype.equals(Float[].class)) {
				Float[] objV=(Float[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i].toString();
				}
				return values;
			}else if (objtype.equals(Byte[].class)) {
				Byte[] objV=(Byte[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i].toString();
				}
				return values;
			}else if (objtype.equals(Short[].class)) {
				Short[] objV=(Short[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i].toString();
				}
				return values;
			}else if (objtype.equals(Long[].class)) {
				Long[] objV=(Long[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i].toString();
				}
				return values;
			}else if (objtype.equals(Character[].class)) {
				Character[] objV=(Character[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i].toString();
				}
				return values;
			}else if(objtype.equals(Boolean[].class)){
				Boolean[] objV=(Boolean[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i].toString();
				}
				return values;
			}else if(object.equals(int[].class)) {
				int[] objV=(int[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i]+"";
				}
				return values;
			}else if(object.equals(double[].class)) {
				double[] objV=(double[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i]+"";
				}
				return values;
			}else if(object.equals(float[].class)) {
				float[] objV=(float[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i]+"";
				}
				return values;
			}else if(object.equals(byte[].class)) {
				byte[] objV=(byte[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i]+"";
				}
				return values;
			}else if(object.equals(short[].class)) {
				short[] objV=(short[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i]+"";
				}
				return values;
			}else if(object.equals(long[].class)) {
				long[] objV=(long[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i]+"";
				}
				return values;
			}else if(object.equals(char[].class)) {
				char[] objV=(char[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i]+"";
				}
				return values;
			}else if(object.equals(boolean[].class)) {
				boolean[] objV=(boolean[])object;
				String[] values=new String[objV.length];
				for (int i = 0; i < values.length; i++) {
					values[i]=objV[i]+"";
				}
				return values;
			}
			
			
			else {
				System.err.println("\n***************请填写基本类型！***************\n");
				return null;
			}
			
	}
	public Object StringToObject(Type type,Object object) {
		if (type.equals(Integer.class)) {
			return Integer.parseInt(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if (type.equals(Double.class)) {
			return Double.parseDouble(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if (type.equals(String.class)) {
			return ArrayParseToString(object)[0];
		}else if (type.equals(Float.class)) {
			return Float.parseFloat(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if (type.equals(Byte.class)) {
			return Byte.parseByte(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if (type.equals(Short.class)) {
			return Short.parseShort(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if (type.equals(Long.class)) {
			return Long.parseLong(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if (type.equals(Character.class)) {
			return (Character)(numberAutoValue(ArrayParseToString(object)[0]).toString()).charAt(0);
		}else if (type.equals(Boolean.class)){
			return Boolean.parseBoolean(booleanAutoValue(ArrayParseToString(object)[0]).toString());
		}else if(type.equals(int.class)){
			return (int)Integer.parseInt(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if(type.equals(double.class)){
			return (double)Double.parseDouble(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if(type.equals(float.class)){
			return (float)Float.parseFloat(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if(type.equals(byte.class)){
			return (byte)Byte.parseByte(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if(type.equals(short.class)){
			return (short)Short.parseShort(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if(type.equals(long.class)){
			return (long)Long.parseLong(numberAutoValue(ArrayParseToString(object)[0]).toString());
		}else if(type.equals(char.class)){
			return (numberAutoValue(ArrayParseToString(object)[0]).toString()).charAt(0);
		}else if(type.equals(boolean.class)){
			return (boolean)Boolean.parseBoolean(booleanAutoValue(ArrayParseToString(object)[0]).toString());
		}
		
		
		else {
			System.err.println("\n***************请填写基本类型！***************\n");
			return null;
		}
	}
	public Object ArrayStringToObject(Type type,String[] object) {
		if (type.equals(Integer[].class)) {
			Integer[] objV=new Integer[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=Integer.parseInt(numberAutoValue(object[i]).toString());
			}
			return objV;
			
		}else if (type.equals(Double[].class)) {
			Double[] objV=new Double[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=Double.parseDouble(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if (type.equals(String[].class)) {
			return object;
		}else if (type.equals(Float[].class)) {
			Float[] objV=new Float[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=Float.parseFloat(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if (type.equals(Byte[].class)) {
			Byte[] objV=new Byte[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=Byte.parseByte(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if (type.equals(Short[].class)) {
			Short[] objV=new Short[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=Short.parseShort(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if (type.equals(Long[].class)) {
			Long[] objV=new Long[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=Long.parseLong(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if (type.equals(Character[].class)) {
			Character[] objV=new Character[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=(Character)numberAutoValue(object[i]).toString().charAt(0);
			}
			return objV;
		}else if (type.equals(Boolean[].class)) {
			Boolean[] objV=new Boolean[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=Boolean.parseBoolean(booleanAutoValue(object[i]).toString());
			}
			return objV;
		}else if(type.equals(int[].class)){
			int[] objV=new int[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=(int)Integer.parseInt(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if(type.equals(double[].class)){
			double[] objV=new double[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=(double)Double.parseDouble(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if(type.equals(float[].class)){
			float[] objV=new float[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=(float)Float.parseFloat(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if(type.equals(byte[].class)){
			byte[] objV=new byte[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=(byte)Byte.parseByte(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if(type.equals(short[].class)){
			short[] objV=new short[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=(short)Short.parseShort(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if(type.equals(long[].class)){
			long[] objV=new long[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=(long)Long.parseLong(numberAutoValue(object[i]).toString());
			}
			return objV;
		}else if(type.equals(char[].class)){
			char[] objV=new char[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=numberAutoValue(object[i]).toString().charAt(0);
			}
			return objV;
		}else if(type.equals(boolean[].class)){
			boolean[] objV=new boolean[object.length];
			for (int i = 0; i < objV.length; i++) {
				objV[i]=(boolean)Boolean.parseBoolean(booleanAutoValue(object[i]).toString());
			}
			return objV;
		}
		
		
		
		else {
			System.err.println("\n***************请填写基本类型！***************\n");
			return null;
		}
		
	}
	public Object numberAutoValue(Object object) {
		if (object==null||"".equals(object.toString())) {
			return "0";
		}else {
			return object;
		}
	}
	public Object booleanAutoValue(Object object) {
		if(object==null){
			return false;
		}
		if ("1".equals(object.toString())||"ok".equalsIgnoreCase(object.toString())||"yes".equalsIgnoreCase(object.toString())||"true".equals(object.toString())) {
			return true;
		} else {
			return false;
		}
	}
	
}
