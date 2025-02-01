import 'package:flutter/material.dart';
import 'package:pview/main.dart';

class RainbowDrawScreen extends StatefulWidget {
  const RainbowDrawScreen({super.key});

  @override
  State<RainbowDrawScreen> createState() => _RainbowDrawScreenState();
}

class _RainbowDrawScreenState extends State<RainbowDrawScreen> {
  final List<RainBowStroke> strokes = [];
  RainBowStroke? currentStroke;
  Size androidViewSize = const Size(3860, 2160);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: GestureDetector(
        onPanStart: (details) {
          setState(() {
            currentStroke = RainBowStroke(points: [details.localPosition]);
            strokes.add(currentStroke!);
          });
        },
        onPanUpdate: (details) {
          setState(() {
            currentStroke!.points.add(details.localPosition);
          });
        },
        onPanEnd: (details) {
          setState(() {
            currentStroke = null;
          });
        },
        child: CustomPaint(
          painter: ToolsPainterr(
            strokes: strokes,
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
    // Calculate the diagonal length of the screen as a reference
    final screenDiagonal = size.width.abs() + size.height.abs();
    // Use a larger fraction for smoother transitions
    final rainbowCycleLength = screenDiagonal / 2; // Made cycle longer for smoother transitions

    for (final stroke in strokes) {
      if (stroke.points.length < 2) continue;

      final path = Path();
      path.moveTo(stroke.points[0].dx, stroke.points[0].dy);

      final rainbowColors = [
        const Color.fromARGB(255, 138, 43, 226), // Violet
        const Color.fromARGB(255, 75, 0, 130), // Indigo
        const Color.fromARGB(255, 0, 0, 255), // Blue
        const Color.fromARGB(255, 0, 128, 0), // Green
        const Color.fromARGB(255, 255, 255, 0), // Yellow
        const Color.fromARGB(255, 255, 165, 0), // Orange
        const Color.fromARGB(255, 255, 0, 0), // Red
      ];

      final rotatedColors = [...rainbowColors.sublist(stroke.colorStartIndex), ...rainbowColors.sublist(0, stroke.colorStartIndex)];

      // Draw the stroke as a continuous path instead of individual lines
      final strokePath = Path();
      strokePath.moveTo(stroke.points[0].dx, stroke.points[0].dy);

      // Use quadratic bezier curves for smoother path
      for (int i = 1; i < stroke.points.length - 1; i++) {
        final p0 = stroke.points[i - 1];
        final p1 = stroke.points[i];
        final p2 = stroke.points[i + 1];

        // Calculate control points for smooth curve
        final controlPoint = Offset(
          p1.dx,
          p1.dy,
        );

        final endPoint = Offset(
          (p1.dx + p2.dx) / 2,
          (p1.dy + p2.dy) / 2,
        );

        strokePath.quadraticBezierTo(
          controlPoint.dx,
          controlPoint.dy,
          endPoint.dx,
          endPoint.dy,
        );

        // Calculate position for coloring using interpolated points
        final absolutePosition = (p1.dx.abs() + p1.dy.abs() + endPoint.dx.abs() + endPoint.dy.abs()) / 2;
        final position = (absolutePosition % rainbowCycleLength) / rainbowCycleLength;

        // Use double interpolation for smoother color transitions
        final colorPosition = position * (rotatedColors.length - 1);
        final colorIndex = colorPosition.floor();
        final nextColorIndex = (colorIndex + 1) % rotatedColors.length;
        final colorProgress = colorPosition - colorIndex;

        // Double interpolation for smoother transitions
        final midColor1 = Color.lerp(
          rotatedColors[colorIndex],
          rotatedColors[nextColorIndex],
          colorProgress,
        )!;
        final midColor2 = Color.lerp(
          rotatedColors[nextColorIndex],
          rotatedColors[(nextColorIndex + 1) % rotatedColors.length],
          colorProgress,
        )!;
        final finalColor = Color.lerp(midColor1, midColor2, colorProgress / 2)!;

        final paint = Paint()
          ..color = finalColor
          ..strokeWidth = stroke.width
          ..strokeCap = StrokeCap.round
          ..strokeJoin = StrokeJoin.round
          ..style = PaintingStyle.stroke
          ..isAntiAlias = true; // Ensure anti-aliasing is enabled

        // Draw segment
        canvas.drawPath(strokePath, paint);

        // Start new path from current endpoint
        strokePath.reset();
        strokePath.moveTo(endPoint.dx, endPoint.dy);
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
  final int colorStartIndex;

  RainBowStroke({
    required this.points,
    this.width = 30.0,
    int? colorStartIndex,
  }) : colorStartIndex = colorStartIndex ?? (DateTime.now().millisecondsSinceEpoch % 7);

  Map<String, dynamic> toJson() {
    return {
      'points': points.map((p) => {'x': p.dx, 'y': p.dy}).toList(),
      'width': width,
      'colorStartIndex': colorStartIndex,
    };
  }

  factory RainBowStroke.fromJson(Map<String, dynamic> json) {
    return RainBowStroke(
      points: (json['points'] as List).map((p) => Offset(p['x'] as double, p['y'] as double)).toList(),
      width: json['width'] as double,
      colorStartIndex: json['colorStartIndex'] as int,
    );
  }
}
