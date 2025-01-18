import 'package:flutter/material.dart';
import 'package:pview/main.dart';

class RainbowDrawScreen extends StatefulWidget {
  const RainbowDrawScreen({super.key});

  @override
  State<RainbowDrawScreen> createState() => _RainbowDrawScreenState();
}

class _RainbowDrawScreenState extends State<RainbowDrawScreen> {
  final List<List<Offset>> strokes = [];
  List<Offset>? currentStroke;
  Size androidViewSize = const Size(3860, 2160);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: GestureDetector(
        onPanStart: (details) {
          setState(() {
            currentStroke = [details.localPosition];
            strokes.add(currentStroke!);
          });
        },
        onPanUpdate: (details) {
          setState(() {
            currentStroke!.add(details.localPosition);
          });
        },
        onPanEnd: (details) {
          setState(() {
            currentStroke = null;
          });
        },
        child: CustomPaint(
          painter: ToolsPainterr(
            strokes: strokes.map((points) => RainBowStroke(points: points)).toList(),
            androidViewSize: androidViewSize,
          ),
          size: const Size(3860, 2160),
        ),
      ),
    );
  }
}

class ToolsPainterr extends CustomPainter {
  final List<RainBowStroke> strokes;
  final Size? androidViewSize;

  ToolsPainterr({
    required this.strokes,
    this.androidViewSize,
  });

  @override
  void paint(Canvas canvas, Size size) {
    for (final stroke in strokes) {
      if (stroke.points.length < 2) continue;

      final path = Path();
      path.moveTo(stroke.points[0].dx, stroke.points[0].dy);

      // Create segments of 70 points for rainbow effect
      const pointsPerRainbow = 70;
      final rainbowColors = [
        const Color.fromARGB(255, 138, 43, 226), // Violet
        const Color.fromARGB(255, 75, 0, 130), // Indigo
        const Color.fromARGB(255, 0, 0, 255), // Blue
        const Color.fromARGB(255, 0, 128, 0), // Green
        const Color.fromARGB(255, 255, 255, 0), // Yellow
        const Color.fromARGB(255, 255, 165, 0), // Orange
        const Color.fromARGB(255, 255, 0, 0), // Red
      ];

      final colorsPerSegment = pointsPerRainbow ~/ rainbowColors.length;

      for (int i = 1; i < stroke.points.length; i++) {
        // Calculate which rainbow cycle and color segment we're in
        final rainbowCycle = i ~/ pointsPerRainbow;
        final segmentIndex = (i % pointsPerRainbow) ~/ colorsPerSegment;
        final nextSegmentIndex = ((i % pointsPerRainbow) ~/ colorsPerSegment + 1) % rainbowColors.length;

        // Calculate progress within current color segment
        final segmentProgress = ((i % pointsPerRainbow) % colorsPerSegment) / colorsPerSegment;

        // Create gradient color
        final currentColor = Color.lerp(
          rainbowColors[segmentIndex],
          rainbowColors[nextSegmentIndex],
          segmentProgress,
        )!;

        final paint = Paint()
          ..color = currentColor
          ..strokeWidth = stroke.width
          ..strokeCap = StrokeCap.round
          ..strokeJoin = StrokeJoin.round
          ..style = PaintingStyle.stroke;

        // Draw line segment with current color
        canvas.drawLine(
          stroke.points[i - 1],
          stroke.points[i],
          paint,
        );
      }
    }
  }

  @override
  bool shouldRepaint(ToolsPainterr oldDelegate) {
    return true;
  }
}

class RainBowStroke {
  final List<Offset> points;
  final double width;

  RainBowStroke({
    required this.points,
    this.width = 30.0,
  });

  Map<String, dynamic> toJson() {
    return {
      'points': points.map((p) => {'x': p.dx, 'y': p.dy}).toList(),
      'width': width,
    };
  }

  factory RainBowStroke.fromJson(Map<String, dynamic> json) {
    return RainBowStroke(
      points: (json['points'] as List).map((p) => Offset(p['x'] as double, p['y'] as double)).toList(),
      width: json['width'] as double,
    );
  }
}
