import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:pview/models/stroke.dart';
import 'package:flutter_colorpicker/flutter_colorpicker.dart';
import 'dart:ui' as ui;

enum PenType {
  normal,
  highlighter,
  dashed,
}

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
  List<Stroke> strokes = [];
  Size? androidViewSize;
  Color currentColor = Colors.black;
  double currentWidth = 5.0;
  PenType currentPenType = PenType.normal;
  static const double minStrokeWidth = 1.0;
  static const double maxStrokeWidth = 20.0;
  static const double highlighterWidth = 12.0;
  static const defaultHighlighterColor = Color(0xFFF2F200);
  static const defaultHighlighterAlpha = 75;

  _togglePen() {
    setState(() {
      isPenEnabled = !isPenEnabled;
      if (isPenEnabled) {
        // Update pen settings when enabling the pen
        Future.delayed(const Duration(milliseconds: 100), () {
          _updatePenSettings();
        });
      }
    });
  }

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
              size: const Size(3860, 2160),
            ),
            if (isPenEnabled)
              AndroidView(
                viewType: 'custom_canvas_view',
                creationParams: {
                  'color': currentColor.value,
                  'width': currentWidth,
                  'isDashed': currentPenType == PenType.dashed,
                },
                creationParamsCodec: const StandardMessageCodec(),
                onPlatformViewCreated: (int id) {
                  _channel = MethodChannel('custom_canvas_view_$id');
                  _channel?.setMethodCallHandler(_handleMethodCall);
                  _updatePenSettings();
                },
              ),
            Positioned(
              bottom: 40,
              right: 200,
              child: Row(
                children: [
                  FloatingActionButton(
                    onPressed: () {
                      setState(() {
                        currentPenType = PenType.normal;
                        _updatePenSettings();
                      });
                    },
                    backgroundColor: currentPenType == PenType.normal ? Colors.black : Colors.white,
                    child: Icon(
                      Icons.edit,
                      color: currentPenType == PenType.normal ? Colors.white : Colors.black,
                    ),
                  ),
                  const SizedBox(width: 10),
                  FloatingActionButton(
                    onPressed: () {
                      setState(() {
                        currentPenType = PenType.highlighter;
                        _updatePenSettings();
                      });
                    },
                    backgroundColor: currentPenType == PenType.highlighter ? Colors.yellow[200] : Colors.white,
                    child: Icon(
                      Icons.highlight_alt,
                      color: currentPenType == PenType.highlighter ? Colors.orange : Colors.black,
                    ),
                  ),
                  const SizedBox(width: 10),
                  FloatingActionButton(
                    onPressed: () {
                      setState(() {
                        currentPenType = PenType.dashed;
                        _updatePenSettings();
                      });
                    },
                    backgroundColor: currentPenType == PenType.dashed ? Colors.blue : Colors.white,
                    child: Icon(
                      Icons.line_style,
                      color: currentPenType == PenType.dashed ? Colors.white : Colors.black,
                    ),
                  ),
                  const SizedBox(width: 10),
                  FloatingActionButton(
                    onPressed: () {
                      showDialog(
                        context: context,
                        builder: (BuildContext context) {
                          return AlertDialog(
                            title: const Text('Pick a color'),
                            content: SingleChildScrollView(
                              child: ColorPicker(
                                pickerColor: currentColor,
                                onColorChanged: (Color color) {
                                  setState(() {
                                    currentColor = color;
                                  });
                                },
                                showLabel: true,
                                pickerAreaHeightPercent: 0.8,
                              ),
                            ),
                            actions: <Widget>[
                              TextButton(
                                child: const Text('Done'),
                                onPressed: () {
                                  _updatePenSettings();
                                  Navigator.of(context).pop();
                                },
                              ),
                            ],
                          );
                        },
                      );
                    },
                    backgroundColor: currentColor,
                    child: const Icon(Icons.color_lens, color: Colors.white),
                  ),
                  const SizedBox(width: 10),
                  Container(
                    width: 200,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(30),
                    ),
                    child: Slider(
                      value: currentWidth,
                      min: minStrokeWidth,
                      max: maxStrokeWidth,
                      divisions: 9,
                      label: currentWidth.round().toString(),
                      onChanged: (double value) {
                        setState(() {
                          currentWidth = value;
                          _updatePenSettings();
                        });
                      },
                    ),
                  ),
                ],
              ),
            ),
            Positioned(
              bottom: 40,
              right: 120,
              child: FloatingActionButton(
                onPressed: _togglePen,
                backgroundColor: isPenEnabled ? Colors.black : Colors.white,
                child: Icon(
                  isPenEnabled ? Icons.edit : Icons.edit_off,
                  color: isPenEnabled ? Colors.red : Colors.red,
                ),
              ),
            ),
            Positioned(
              bottom: 40,
              right: 40,
              child: FloatingActionButton(
                onPressed: () {
                  setState(() {
                    strokes.clear();
                  });
                  _channel?.invokeMethod('clear');
                },
                backgroundColor: Colors.white,
                child: const Icon(
                  Icons.delete_outline,
                  color: Colors.red,
                ),
              ),
            ),
          ],
        );
      },
    );
  }

  void _updatePenSettings() {
    if (_channel != null) {
      final color = currentPenType == PenType.highlighter ? (currentColor == Colors.black ? defaultHighlighterColor : currentColor).withAlpha(defaultHighlighterAlpha) : currentColor;
      final width = currentPenType == PenType.highlighter ? highlighterWidth : currentWidth;

      // First update pen settings
      _channel!.invokeMethod('updatePenSettings', {
        'color': color.value,
        'width': width,
      });

      // Then update dashed state
      _channel!.invokeMethod('setDashed', {
        'dashed': currentPenType == PenType.dashed,
      }).then((_) {
        print('Flutter: Updated pen type to ${currentPenType.name}, dashed: ${currentPenType == PenType.dashed}');
      });
    } else {
      print('Flutter: Channel is null, cannot update pen settings');
    }
  }

  Future<dynamic> _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'onStrokeComplete':
        try {
          final strokeData = Map<String, dynamic>.from(call.arguments);
          final stroke = Stroke.fromJson(strokeData);
          setState(() {
            if (currentPenType == PenType.dashed) {
              strokes.add(DashedStroke(
                points: stroke.points,
                color: currentColor,
                width: currentWidth,
              ));
            } else {
              strokes.add(Stroke(
                points: stroke.points,
                color: currentPenType == PenType.highlighter ? (currentColor == Colors.black ? defaultHighlighterColor : currentColor).withAlpha(defaultHighlighterAlpha) : currentColor,
                width: currentPenType == PenType.highlighter ? highlighterWidth : currentWidth,
                isDashed: false,
              ));
            }
          });
        } catch (e) {
          print('Error processing stroke data: $e');
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

      if (stroke is DashedStroke) {
        final path = Path();
        path.moveTo(stroke.points[0].dx, stroke.points[0].dy);
        for (int i = 1; i < stroke.points.length; i++) {
          path.lineTo(stroke.points[i].dx, stroke.points[i].dy);
        }

        final pathMetrics = path.computeMetrics();
        final dashedPath = Path();

        for (final metric in pathMetrics) {
          var distance = 0.0;
          final length = metric.length;

          while (distance < length) {
            // Draw dash
            dashedPath.addPath(
              metric.extractPath(distance, distance + 30),
              Offset.zero,
            );
            // Skip gap
            distance += 50; // 30 (dash) + 20 (gap)
          }
        }

        canvas.drawPath(dashedPath, paint);
      } else {
        final path = Path();
        path.moveTo(stroke.points[0].dx, stroke.points[0].dy);

        for (int i = 1; i < stroke.points.length; i++) {
          path.lineTo(stroke.points[i].dx, stroke.points[i].dy);
        }

        canvas.drawPath(path, paint);
      }
    }
  }

  @override
  bool shouldRepaint(ToolsPainter oldDelegate) {
    return true;
  }
}
