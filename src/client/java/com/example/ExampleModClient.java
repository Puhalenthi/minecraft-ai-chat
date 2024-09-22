package com.example;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("conjure")
                .then(ClientCommandManager.argument("prompt", StringArgumentType.string())
                .executes(context -> {
                    String prompt = StringArgumentType.getString(context, "prompt");
                    context.getSource().sendFeedback(Text.literal("You conjured: " + prompt));

                    var username = MinecraftClient.getInstance().getSession().getUsername();
                    new Thread(() -> {
                    try {
                        var client = HttpClient.newHttpClient();
                        var query = String.format("prompt=%s&username=%s",
                                    URLEncoder.encode(prompt, "UTF-8"),
                                    URLEncoder.encode(username, "UTF-8"));
    
                        var request = HttpRequest.newBuilder()
                                    .uri(new URI("http://localhost:5000/mcchatbot?" + query))
                                    .GET()
                                    .build();
    
                        var command = client.send(request, BodyHandlers.ofString()).body();
    
                        
    
                        if (command.startsWith("/")) { // If GPT returns a command with a forward slash, omit it
                            MinecraftClient.getInstance().getNetworkHandler().sendCommand(command.substring(1));
                        }
                        else {
                            MinecraftClient.getInstance().getNetworkHandler().sendCommand("\\" + command.substring(1));
                        }
                    } catch (Exception e) {
                        return;
                    }
                }).start();

                    return 1;
                }))
            );
        });
    }
}
