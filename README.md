# Minecraft AI Helper
A simple AI utility that converts a prompt using natural language into a Minecraft command

## Setup
> [!IMPORTANT]  
> Must have access to the OpenAI API.
  - Clone repo: `git clone https://github.com/Puhalenthi/minecraft-ai-chat.git`
  - Create a GitHub PAT and put it inside a .env: `GITHUB_TOKEN=[TOKEN]`

## How to run
  - Start python api: `python api.py`
  - Run gradle: `./gradlew runClient()`

## How to use
`\conjure [prompt]`

Ex:
  - `\conjure "Summon all entities to my location"`
  - `\conjure "Give me a netherite sword with level 10 sharpness and level 17 knockback"`
  - `\conjure "Bestow upon me all status affects at this very moment!"`
