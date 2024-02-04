package saltsheep.keylib;

import net.minecraftforge.common.config.Config;

@Config(modid = SheepKeyLib.MODID)
public class SheepKeyLibConfig {

	@Config.Comment("是否使用鼠标，这会极大消耗网络资源")
	public static boolean recordMouse = false;
	
}
