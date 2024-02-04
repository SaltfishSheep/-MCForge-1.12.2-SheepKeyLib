package saltsheep.keylib.key;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerKeyUpEvent extends Event {
	
	public final EntityPlayerMP player;
	public final int key;
	public final long time;
	
	public PlayerKeyUpEvent(String playerName, int key, long time) {
		this(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName),key,time);
	}
	
	public PlayerKeyUpEvent(EntityPlayerMP player, int key, long time) {
		this.player = player;
		this.key = key;
		this.time = time;
	}

}
