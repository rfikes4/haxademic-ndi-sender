NDI Sender for Haxademic
=========
Creates a NDI instance for Processing to send PGraphics/PImages over the network. Tested using JDK-13.

Uses Devolay https://github.com/WalkerKnapp/devolay
The .jar can be downloaded at https://dl.bintray.com/walkerknapp/devolay/com/walker/devolay/0.0.1/:devolay-0.0.1.jar

Download and install NDI SKD https://ndi.tv/sdk/
Download and install NDI Tools https://ndi.tv/tools/

### Interactions

- Key 1 only displays the Processing Spout sending texture on the Processing side
- Key 2 changes Processing Spout sending texture's color
- Mouse rotates the GameObject with **WebSocketGameObjectController.cs**

Unity Setup
-------------------
Import **UnityProcessingWebSocketSpout.unitypackage** into Unity (Assets > Import Package > Custom Package) and open the scene **UnityProcessingWebSocketSpout** (Scenes > UnityProcessingWebSocketSpout).

### Initializer

![initializer](https://user-images.githubusercontent.com/20564801/65345705-1c6e1880-db98-11e9-9e98-b7101601ab2d.jpg)
- Set width/height of build file (.exe) window. Also creates a Spout sending texture at that size
- Can stretch the .exe to fullscreen
- Can minimize .exe on start
- **Important:** Make sure the **Spout Sender** GameObject is referenced, the script will create a RenderTexture for it's source script

### WebSocketClient

![webSocketClient](https://user-images.githubusercontent.com/20564801/65348866-5bec3300-db9f-11e9-8f76-b4be5d987e87.jpg)

- Uncheck **Use Web Socket** to disable WebSocket/Spout connection (good for normal debugging purposes)

### SpoutReceiver

![spoutReceiver](https://user-images.githubusercontent.com/20564801/65349272-40355c80-dba0-11e9-8492-1667fc8c1820.jpg)

- All processing drawing for Spout sending and receiving textures are in **UnityProcessingSpout.java**
- To recieve textures from Processing add this script (Plugins > Spout > Runtime > SpoutReceiver), set the Source Name to the same set string **spoutSenderName** in **UnityProcessingSpout.java**, and set the **Target Texture** to the **SpoutReceiver** RenderTexture, and finally drag **SpoutReceiver** RenderTexture onto the GameObject which should update it's material to **SpoutReceiver** (if the material is not added you can find it in Materials > SpoutReceiver)
