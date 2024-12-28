package igentuman.nc.client.particle;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;

import static igentuman.nc.NuclearCraft.rl;

public class ShaderLoader {
    private static ShaderInstance blackHoleShader;

    public static void loadShaders() {
        try {
            blackHoleShader = new ShaderInstance(
                Minecraft.getInstance().getResourceManager(),
                rl( "blackhole_distortion"),
                    DefaultVertexFormat.POSITION_TEX_COLOR
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ShaderInstance getBlackHoleShader() {
        if(blackHoleShader == null) {
            loadShaders();
        }
        return blackHoleShader;
    }
}
