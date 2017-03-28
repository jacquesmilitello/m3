package io.m3.sql.apt;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;
import java.io.Writer;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class Helper {


    public static void writeNewLine(Writer writer) throws IOException {
        writer.write(System.getProperty("line.separator"));
    }

    public static void writeNewLine(StringBuilder builder) {
        builder.append(System.getProperty("line.separator"));
    }

    public static void writePackage(Writer writer, String pack) throws IOException {
        writer.write("package ");
        writer.write(pack);
        writer.write(';');
        writeNewLine(writer);
    }

    public static void writeGenerated(Writer writer, String clazz) throws IOException {
        writeNewLine(writer);
        writer.write("import javax.annotation.Generated;");
        writeNewLine(writer);
        writeNewLine(writer);

        writer.write("@Generated(\"" + clazz + "\")");
        writeNewLine(writer);
    }

    public static String variable(String name) {
        StringBuilder builder = new StringBuilder(name.length());
        builder.append(Character.toLowerCase(name.charAt(0)));
        builder.append(name.substring(1));
        return builder.toString();
    }

    public static Map<String, List<PojoDescriptor>> analyse(ProcessingEnvironment env, List<PojoDescriptor> descriptors) {

        Map<String, List<PojoDescriptor>> map = new HashMap<>();

        descriptors.forEach(desc -> {
            String pack = env.getElementUtils().getPackageOf(desc.element()).toString();

            List<PojoDescriptor> list = map.get(pack);

            if (list == null) {
                list = new ArrayList<>();
                map.put(pack, list);
            }

            list.add(desc);
        });

        return map;
    }

    public static String toUpperCase(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toUpperCase(name.charAt(0)));
        for (int i = 1 ; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append('_');
            }
            builder.append(Character.toUpperCase(c));
        }
        return builder.toString();
    }

    public static String preparedStatementSetter(String type) {

        if ("int".equals(type) || "java.lang.Integer".equals(type)) {
            return "setInt";
        }

        if ("long".equals(type) || "java.lang.Long".equals(type)) {
            return "setLong";
        }

        if ("short".equals(type) || "java.lang.Short".equals(type)) {
            return "setShort";
        }

        if ("short".equals(type) || "java.lang.Short".equals(type)) {
            return "setShort";
        }

        if ("java.lang.String".equals(type)) {
            return "setString";
        }

        if ("java.sql.Date".equals(type)) {
            return "setDate";
        }

        throw new UnsupportedOperationException("Helper.preparedStatementSetter -> type not supported -> [" + type + "]");
    }

    public static String preparedStatementGetter(String type) {

        if ("int".equals(type) || "java.lang.Integer".equals(type)) {
            return "getInt";
        }

        if ("long".equals(type) || "java.lang.Long".equals(type)) {
            return "getLong";
        }

        if ("short".equals(type) || "java.lang.Short".equals(type)) {
            return "getShort";
        }

        if ("byte".equals(type) || "java.lang.Byte".equals(type)) {
            return "getByte";
        }

        if ("java.lang.String".equals(type)) {
            return "getString";
        }

        if ("java.sql.Date".equals(type)) {
            return "getDate";
        }

        throw new UnsupportedOperationException("Helper.preparedStatementSetter -> type not supported -> [" + type + "]");
    }

    public static boolean isNullableType(String type) {
        if ("int".equals(type) || "short".equals(type) || "long".equals(type) || "byte".equals(type) || "int".equals(type)) {
            return false;
        }
        return true;
    }

    public static String nullableType(String type) {

        if ( "java.lang.Integer".equals(type)) {
            return "java.sql.Types.INTEGER";
        }

        if ("java.lang.Long".equals(type)) {
            return "java.sql.Types.BIGINT";
        }

        if ("short".equals(type) || "java.lang.Short".equals(type)) {
            return "java.sql.Types.SMALLINT";
        }

        if ("byte".equals(type) || "java.lang.Byte".equals(type)) {
            return"java.sql.Types.TINYINT";
        }

        throw new UnsupportedOperationException("Helper.nullableType -> type not supported -> [" + type + "]");

    }
}
