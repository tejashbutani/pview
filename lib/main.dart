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
      home: const DrawingScreen(),
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

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [

        // Flutter canvas overlay for additional tools
        CustomPaint(
          painter: ToolsPainter(),
          child: GestureDetector(
            onPanUpdate: (details) {
              // Handle tool interactions
            },
          ),
        ),

         // Hardware accelerated native view
        AndroidView(
          viewType: 'custom_canvas_view',
          onPlatformViewCreated: (int id) {
            _channel = MethodChannel('custom_canvas_view_$id');
          },
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
