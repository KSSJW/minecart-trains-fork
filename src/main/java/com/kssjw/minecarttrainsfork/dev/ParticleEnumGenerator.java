package com.kssjw.minecarttrainsfork.dev;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import java.lang.reflect.Field;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParticleEnumGenerator {
    
    public static void generateEnum() {
        try {
            List<String> names = new ArrayList<>();

            for (Field field : ParticleTypes.class.getFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    if (SimpleParticleType.class.isAssignableFrom(field.getType())) {
                        names.add(field.getName());
                    }
                }
            }

            Collections.sort(names);

            try (FileWriter writer = new FileWriter("ListUtil.java")) {
                writer.write("package com.kssjw.minecarttrainsfork.client.util;\n\n");
                writer.write("import net.minecraft.particle.ParticleTypes;\n");
                writer.write("import net.minecraft.particle.SimpleParticleType;\n\n");
                writer.write("public class ListUtil {\n\n");
                writer.write("    private ListUtil() {}\n\n");
                writer.write("    public enum ParticleOption {\n");

                for (int i = 0; i < names.size(); i++) {
                    String name = names.get(i).toLowerCase(); // 枚举常量名小写
                    String type = names.get(i);               // 对应 ParticleTypes 常量
                    boolean isLast = (i == names.size() - 1);

                    writer.write("        " + name + "(ParticleTypes." + type + ")" + (isLast ? ";" : ",") + "\n");
                }

                writer.write("\n        private final SimpleParticleType type;\n\n");
                writer.write("        ParticleOption(SimpleParticleType type) {\n");
                writer.write("          this.type = type;\n");
                writer.write("        }\n\n");
                writer.write("        public SimpleParticleType getType() {\n");
                writer.write("          return type;\n");
                writer.write("        }\n");
                writer.write("    }\n");
                writer.write("}");
            }

            System.out.println("已生成 ParticleOption.java 枚举类源码。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}