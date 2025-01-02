import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:pview/models/stroke.dart';

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
  final List<Stroke> strokes = [];
  Size? androidViewSize;

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        androidViewSize = Size(constraints.maxWidth, constraints.maxHeight);

        return Stack(
          children: [
            CustomPaint(
              painter: ToolsPainter(
                strokes: strokes,
                androidViewSize: androidViewSize,
              ),
              size: Size(constraints.maxWidth, constraints.maxHeight),
            ),
            if (isPenEnabled)
              AndroidView(
                viewType: 'custom_canvas_view',
                onPlatformViewCreated: (int id) {
                  _channel = MethodChannel('custom_canvas_view_$id');
                  _channel?.setMethodCallHandler(_handleMethodCall);
                },
              ),
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
      },
    );
  }

  Future<dynamic> _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'onStrokeComplete':
        try {
          final strokeData = Map<String, dynamic>.from(call.arguments);
          final stroke = Stroke.fromJson(strokeData);
          setState(() {
            strokes.add(stroke);
          });
          print('Received stroke with ${stroke.points.length} points'); // Debug log
        } catch (e) {
          print('Error processing stroke data: $e'); // Debug log
        }
        break;
    }
  }
}

class ToolsPainter extends CustomPainter {
  final List<Stroke> strokes;
  final Size? androidViewSize;

  ToolsPainter({
    required this.strokes,
    this.androidViewSize,
  });

  @override
  void paint(Canvas canvas, Size size) {
    for (final stroke in strokes) {
      if (stroke.points.length < 2) continue;

      final paint = Paint()
        ..color = stroke.color
        ..strokeWidth = stroke.width
        ..strokeCap = StrokeCap.round
        ..strokeJoin = StrokeJoin.round
        ..style = PaintingStyle.stroke;

      final path = Path();

      // Scale points if we have Android view size
      if (androidViewSize != null) {
        final scaleX = size.width / androidViewSize!.width;
        final scaleY = size.height / androidViewSize!.height;

        final firstPoint = stroke.points[0];
        path.moveTo(
          firstPoint.dx * scaleX,
          firstPoint.dy * scaleY,
        );

        for (int i = 1; i < stroke.points.length; i++) {
          final point = stroke.points[i];
          path.lineTo(
            point.dx * scaleX,
            point.dy * scaleY,
          );
        }
      } else {
        path.moveTo(stroke.points[0].dx, stroke.points[0].dy);
        for (int i = 1; i < stroke.points.length; i++) {
          path.lineTo(stroke.points[i].dx, stroke.points[i].dy);
        }
      }

      canvas.drawPath(path, paint);
    }
  }

  @override
  bool shouldRepaint(ToolsPainter oldDelegate) => true;
}
