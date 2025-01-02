import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Drawing Canvas Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  MethodChannel? _channel;
  int? _textureId;

  @override
  void initState() {
    super.initState();
    _initTexture();
  }

  Future<void> _initTexture() async {
    final textureId = await _channel?.invokeMethod<int>('initTexture');
    setState(() {
      _textureId = textureId;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Drawing Canvas'),
        actions: [
          IconButton(
            icon: const Icon(Icons.clear),
            onPressed: () {
              _channel?.invokeMethod('clear');
            },
          ),
        ],
      ),
      body: _textureId != null ? Texture(textureId: _textureId!) : const Center(child: CircularProgressIndicator()),
    );
  }
}

// class DrawingCanvas extends StatefulWidget {
//   @override
//   _DrawingCanvasState createState() => _DrawingCanvasState();
// }

// class _DrawingCanvasState extends State<DrawingCanvas> {
//   MethodChannel? _channel;
//   ui.Image? _image;

//   Future<void> _loadBitmap() async {
//     final bytes = await _channel?.invokeMethod<Uint8List>('getBitmap');
//     if (bytes != null) {
//       final codec = await ui.instantiateImageCodec(bytes);
//       final frame = await codec.getNextFrame();
//       setState(() {
//         _image = frame.image;
//       });
//     }
//   }

//   @override
//   Widget build(BuildContext context) {
//     return CustomPaint(
//       painter: BitmapPainter(_image),
//       child: Container(),
//     );
//   }
// }

// class BitmapPainter extends CustomPainter {
//   final ui.Image? image;

//   BitmapPainter(this.image);

//   @override
//   void paint(Canvas canvas, Size size) {
//     if (image != null) {
//       canvas.drawImage(image!, Offset.zero, Paint());
//     }
//   }

//   @override
//   bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
// }
