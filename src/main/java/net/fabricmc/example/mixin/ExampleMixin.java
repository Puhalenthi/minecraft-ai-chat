package net.fabricmc.example.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;

import java.util.regex.Pattern;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatHud.class)
public class ExampleMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("modid");

    

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    private void addMessage(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo info)  {
        LOGGER.info("testing" + message.getString());
        var pattern = Pattern.compile("<([a-zA-Z0-9_]{3,16})> (.+)");
        var matcher = pattern.matcher(message.getString());
        
        if (matcher.find()) {
            var username = matcher.group(1);
            var prompt = matcher.group(2);
            
            if (prompt.toLowerCase().startsWith("conjure:")) {
                new Thread(() -> {
                    try {
                        var finalprompt = prompt.substring(8);
    
                        var client = HttpClient.newHttpClient();
                        var query = String.format("prompt=%s&username=%s",
                                    URLEncoder.encode(finalprompt, "UTF-8"),
                                    URLEncoder.encode(username, "UTF-8"));
    
                        var request = HttpRequest.newBuilder()
                                    .uri(new URI("http://localhost:5000/mcchatbot?" + query))
                                    .GET()
                                    .build();
    
                        var command = client.send(request, BodyHandlers.ofString()).body();
    
                        
                        LOGGER.info("COMMAND RECEIVED: " + command);
                        if (command.startsWith("/")) { // If GPT returns a command with a forward slash, omit it
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command.substring(1));
                        }
                        else {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command);
                        }
                    } catch (Exception e) {
                        return;
                    }
                }).start();
            }
        }
    }
}