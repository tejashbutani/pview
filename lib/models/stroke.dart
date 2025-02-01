import 'package:flutter/material.dart';

class Stroke {
  final List<Offset> points;
  final Color color;
  final double width;
  final bool isDashed;

  Stroke({
    required this.points,
    this.color = Colors.black,
    this.width = 5.0,
    this.isDashed = false,
  });

  Map<String, dynamic> toJson() {
    return {
      'points': points.map((p) => {'x': p.dx, 'y': p.dy}).toList(),
      'color': color.value,
      'width': width,
      'isDashed': isDashed,
    };
  }

  factory Stroke.fromJson(Map<String, dynamic> json) {
    return Stroke(
      points: (json['points'] as List).map((p) => Offset(p['x'] as double, p['y'] as double)).toList(),
      color: Color(json['color'] as int),
      width: json['width'] as double,
      isDashed: json['isDashed'] as bool? ?? false,
    );
  }
}

class DashedStroke extends Stroke {
  DashedStroke({
    required super.points,
    super.color,
    super.width,
  }) : super(
          isDashed: true,
        );
}
