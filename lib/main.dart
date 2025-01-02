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
      home: const Scaffold(
        body: DrawingScreen(),
      ),
    );
  }
}

class DrawingScreen extends StatefulWidget {
  const DrawingScreen({super.key});

  @override
  State<DrawingScreen> createState() => _DrawingScreenState();
}

class _DrawingScreenState extends State<DrawingScreen> {
  MethodChannel? _channel;
  bool isPenEnabled = false;

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        // Flutter canvas overlay for normal drawing
        CustomPaint(
          painter: ToolsPainter(),
          child: GestureDetector(
            onPanUpdate: (details) {
              // Handle regular drawing interactions when pen is not enabled
              if (!isPenEnabled) {
                // Add your drawing logic here
              }
            },
          ),
        ),

        // Android View on top only when pen is enabled
        if (isPenEnabled)
          AndroidView(
            viewType: 'custom_canvas_view',
            onPlatformViewCreated: (int id) {
              _channel = MethodChannel('custom_canvas_view_$id');
            },
          ),

        // Pen toggle button (always on top)
        Positioned(
          bottom: 40,
          right: 40,
          child: FloatingActionButton(
            onPressed: () {
              setState(() {
                isPenEnabled = !isPenEnabled;
              });
            },
            backgroundColor: isPenEnabled ? Colors.blue : Colors.white,
            child: Icon(
              isPenEnabled ? Icons.edit : Icons.edit_off,
              color: isPenEnabled ? Colors.white : Colors.grey,
            ),
          ),
        ),
      ],
    );
  }
}

class ToolsPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    // Draw tool overlays, selection boxes, etc.
  }

  @override
  bool shouldRepaint(CustomPainter oldDelegate) => true;
}
