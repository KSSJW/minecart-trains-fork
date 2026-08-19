package top.windysky.minecarttrainsfork.dev;

import java.lang.reflect.Field;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import top.windysky.minecarttrainsfork.util.LogUtil;

import net.minecraft.SharedConstants;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class ParticleEnumGenerator {

    /**
     * @deprecated For development use only.
     */
    @Deprecated(since = "dev")
    public static void generateEnum() {

        try {
            List<String> names = new ArrayList<>();

            for (Field field : ParticleTypes.class.getFields()) {

                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && SimpleParticleType.class.isAssignableFrom(field.getType())) names.add(field.getName());
            }

            Collections.sort(names);

            try (FileWriter writer = new FileWriter("ParticleList.txt")) {
                writer.write("// " + SharedConstants.getCurrentVersion().id() + "\n");

                for (int i = 0; i < names.size(); i++) {
                    String name = names.get(i).toLowerCase();   // 枚举常量名小写
                    String type = names.get(i); // 对应 ParticleTypes 常量
                    boolean isLast = (i == names.size() - 1);
                    writer.write(name + "(ParticleTypes." + type + ")" + (isLast ? ";" : ",") + "\n");
                }
            }

            LogUtil.print("The enumeration list has been generated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}