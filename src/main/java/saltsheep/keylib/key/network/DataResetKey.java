package saltsheep.keylib.key.network;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import saltsheep.keylib.key.PlayerKeyHandler;

public class DataResetKey {

	private static final String NAME = "SHEEPRESETKEYDATA";
	public static final FMLEventChannel CHANNEL_RESET = NetworkRegistry.INSTANCE.newEventDrivenChannel(NAME);
	
	@SideOnly(Side.CLIENT)
	public static void resetInputByClient() {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeString(Minecraft.getMinecraft().player.getName());
		CHANNEL_RESET.sendToServer(new FMLProxyPacket(buf,NAME));
	}
	
	@SubscribeEvent
	public static void resetByServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
		if(event.getPacket().channel().equals(NAME)) {
			MinecraftServer MCServer = FMLCommonHandler.instance().getMinecraftServerInstance();
			PacketBuffer buf = new PacketBuffer(event.getPacket().payload());
			String playerName = buf.readString(100);
			MCServer.addScheduledTask(()->{
				PlayerKeyHandler.reset(playerName);
			});
		}
	}
	
}
