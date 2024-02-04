package saltsheep.keylib.key.network;

public class NetworkHandler {
	
	public static void register() {
		DataKeyState.CHANNEL.register(DataKeyState.class);
		DataResetKey.CHANNEL_RESET.register(DataResetKey.class);
	}
	
}
