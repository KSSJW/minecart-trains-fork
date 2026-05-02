package com.kssjw.minecarttrainsfork.dev;

import java.lang.reflect.Field;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;

public class ParticleEnumGenerator {
    
    public static void generateEnum() {

        try {
            List<String> names = new ArrayList<>();

            for (Field field : ParticleTypes.class.getFields()) {

                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && SimpleParticleType.class.isAssignableFrom(field.getType())) names.add(field.getName());
            }

            Collections.sort(names);

            try (FileWriter writer = new FileWriter("ParticleList.txt")) {

                for (int i = 0; i < names.size(); i++) {
                    String name = names.get(i).toLowerCase();   // 枚举常量名小写
                    String type = names.get(i); // 对应 ParticleTypes 常量
                    boolean isLast = (i == names.size() - 1);
                    writer.write(name + "(ParticleTypes." + type + ")" + (isLast ? ";" : ",") + "\n");
                }
            }

            System.out.println("已生成枚举类源码。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}