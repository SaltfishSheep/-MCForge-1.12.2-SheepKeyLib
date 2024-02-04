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

public class DataKeyState {

	static final String NAME = "SHEEPKEYSTATEDATA";
	static final FMLEventChannel CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel(NAME);
	
	@SideOnly(Side.CLIENT)
	public static void inputByClient(int key,boolean isDown) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeString(Minecraft.getMinecraft().player.getName());
		buf.writeInt(key);
		buf.writeBoolean(isDown);
		buf.writeLong(System.currentTimeMillis());//*为了向上兼容
		CHANNEL.sendToServer(new FMLProxyPacket(buf,NAME));
	}
	
	@SubscribeEvent
	public static void inputByServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
		if(event.getPacket().channel().equals(NAME)) {
			MinecraftServer MCServer = FMLCommonHandler.instance().getMinecraftServerInstance();
			PacketBuffer buf = new PacketBuffer(event.getPacket().payload());
			String playerName = buf.readString(100);
			int key = buf.readInt();
			boolean isDown = buf.readBoolean();
			long time = buf.readLong();//*为了向上兼容
			MCServer.addScheduledTask(()->{
				if(isDown)
					PlayerKeyHandler.keyDown(playerName, key, time);
				else
					PlayerKeyHandler.keyUp(playerName, key, time);
			});
		}
	}
	
}
