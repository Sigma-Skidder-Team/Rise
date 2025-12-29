import rise.WebSocketClient;
import rise.packet.impl.c2s.community.C2SPacketIRCMessageKt;
import rise.packet.impl.c2s.protection.C2SPacketAuthenticate;
import rise.packet.impl.s2c.community.*;
import rise.packet.impl.s2c.general.S2CPacketKeepAlive;
import rise.packet.impl.s2c.management.S2CPacketCrash;
import rise.packet.impl.s2c.protection.S2CPacketAltLogin;
import rise.packet.impl.s2c.protection.S2CPacketAuthenticationFinish;
import rise.packet.impl.s2c.protection.S2CPacketJoinServer;
import rise.packet.impl.s2c.protection.S2CPacketLoadConfig;

import java.util.Set;

public class JavaExample {
    private record Account(String username, String hwid) {}

    private static void connectAs(Account acc) {
        final var wsc = new WebSocketClient();
        wsc.addHandshakeListener(() -> {
            wsc.send(new C2SPacketAuthenticate(acc.username(), acc.hwid()));
        });
        wsc.addPacketListener(p -> {
            // java 21 allows you to use switch expressions and stuff,
            // but this project is java 17 because I hate myself
            if (p instanceof S2CPacketIRCMessage ircMessage) {
                String msg = ircMessage.getMessage();
                // they append a number to it lol
                String author = ircMessage.getAuthor().substring(1);
                // fun fact: you can use the mc colors in the message, and it'll be colored.
                System.out.println("[IRC] " + author + ": " + msg);
            } else if (p instanceof S2CPacketAuthenticationFinish authFinish) {
                boolean success = authFinish.getSuccess();
                System.out.println((success ? "Successfully" : "Failed to") + " authenticate" + (success ? "d" : "") + "!");
                System.out.println("Reason: " + authFinish.getReason());
                final var aod = authFinish.getAod();
                if (aod != null) {
                    System.out.println("Auth time: " + aod.serverTimeMS());
                    System.out.println("PI: " + aod.pi());
                    System.out.println("Max Pitch: " + aod.maxPitch());
                }
                // very ugly because... java.
                wsc.send(C2SPacketIRCMessageKt.getAsIRCMessage(
                        "Hello from github.com/Sigma-Skidder-Team/Rise!"
                ));
                // we sent our message, disconnect.
                wsc.disconnect();
            } else if (p instanceof S2CPacketKeepAlive) {
                // do nothing, it's a keepalive.
            } else if (p instanceof S2CPacketTabIRC tabIRC) {
                System.out.println("Tab IRC packet: " + tabIRC.getData());
            } else if (p instanceof S2CPacketTitleIRC titleIRC) {
                System.out.println("Title IRC packet: " + titleIRC.getMessage() + " with color " + titleIRC.getColor());
            } else if (p instanceof S2CPacketCommunityInfo communityInfo) {
                System.out.println("Community info with type " + communityInfo.getType());
            } else if (p instanceof S2CPacketJoinServer joinServer) {
                System.out.println("Join Server packet: " + joinServer.getIp() + ":" + joinServer.getPort());
            } else if (p instanceof S2CPacketTroll troll) {
                System.out.println("Troll packet: Reverse Binds " + troll.getReverseKeybinds() + " Aura Disabled " + troll.getKillauraDisabled());
            } else if (p instanceof S2CPacketAltLogin altLogin) {
                System.out.println("Alt Login packet: " + altLogin.getUsername() + " uuid " + altLogin.getUuid() + " RT " + altLogin.getRefreshToken() + " AT " + altLogin.getAccessToken());
            } else if (p instanceof S2CPacketLoadConfig loadConfig) {
                System.out.println("Got load config packet: " + loadConfig.getConfig());
            } else if (p instanceof S2CPacketCrash) {
                System.out.println("Ignoring crash packet");
            } else {
                System.out.println("Unhandled packet: " + p);
            }
        });
        wsc.connect();
    }

    public static void main(String[] args) {
        final var accounts = Set.of(new Account("AuthEnabler", "67"));
        accounts.forEach(JavaExample::connectAs);
    }
}
