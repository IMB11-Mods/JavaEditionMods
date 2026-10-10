package dev.imb11.client.renderer.projection;

import dev.imb11.projection.ProjectionSurface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

final class PortalSide {
    private static final double SIDE_EPSILON = 1.0E-7D;
    private static final double DISTANCE_EPSILON = SIDE_EPSILON * SIDE_EPSILON;
    private static final double CROSSING_EPSILON = 1.0E-6D;

    private ProjectionView view;
    private Vec3 lastViewerPosition;
    private double side;
    private long lastUpdateFrame = Long.MIN_VALUE;

    void track(ProjectionView view, Vec3 viewer, long frame) {
        if (!view.equals(this.view)) {
            boolean frameChanged = this.view != null && !this.view.sameFrame(view);
            this.view = view;
            lastViewerPosition = frameChanged || pointOnSurface(view, viewer) ? null : viewer;
            lastUpdateFrame = Long.MIN_VALUE;
            if (frameChanged) {
                side = 0.0D;
            }
        }
        update(viewer, frame);
    }

    void update(Vec3 viewer, long frame) {
        if (lastUpdateFrame == frame || view == null) {
            return;
        }
        lastUpdateFrame = frame;
        if (pointOnSurface(view, viewer)) {
            if (side == 0.0D) {
                lastViewerPosition = null;
            }
            return;
        }
        if (side == 0.0D) {
            side = classify(view, viewer);
            lastViewerPosition = viewer;
            return;
        }
        Vec3 previous = lastViewerPosition;
        lastViewerPosition = viewer;
        if (previous == null || previous.distanceToSqr(viewer) <= DISTANCE_EPSILON) {
            return;
        }
        if (segmentIntersectsBounds(view.bounds(), previous, viewer)
                && (countSurfaceCrossings(view, previous, viewer) & 1) != 0) {
            side = -side;
        }
    }

    void inheritSide(PortalSide other) {
        side = other.side;
    }

    double resolve(ProjectionView view, Vec3 viewer) {
        return side == 0.0D ? classify(view, viewer) : side;
    }

    private static double classify(ProjectionView view, Vec3 point) {
        double closestDistance = Double.POSITIVE_INFINITY;
        double closestSignedDistance = 0.0D;
        for (ProjectionSurface.Face face : view.faces()) {
            double distance = distanceToFaceSquared(face, point);
            double signedDistance = signedFaceDistance(face, point);
            if (distance < closestDistance - DISTANCE_EPSILON
                    || (Math.abs(distance - closestDistance) <= DISTANCE_EPSILON && signedDistance > closestSignedDistance)) {
                closestDistance = distance;
                closestSignedDistance = signedDistance;
            }
        }
        if (closestDistance == Double.POSITIVE_INFINITY) {
            closestSignedDistance = point.subtract(view.projectorAnchor()).dot(view.normal());
        }
        return closestSignedDistance < 0.0D ? -1.0D : 1.0D;
    }

    private static boolean pointOnSurface(ProjectionView view, Vec3 point) {
        if (!view.bounds().contains(point)
                || (!nearInteger(point.x) && !nearInteger(point.y) && !nearInteger(point.z))) {
            return false;
        }
        for (ProjectionSurface.Face face : view.faces()) {
            if (Math.abs(signedFaceDistance(face, point)) <= SIDE_EPSILON && pointWithinFace(face, point)) {
                return true;
            }
        }
        return false;
    }

    private static int countSurfaceCrossings(ProjectionView view, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        List<Double> parameters = new ArrayList<>();
        for (ProjectionSurface.Face face : view.faces()) {
            Direction.Axis axis = face.normal().getAxis();
            double axisDelta = delta.get(axis);
            if (Math.abs(axisDelta) <= SIDE_EPSILON) {
                continue;
            }
            double parameter = (facePlane(face) - start.get(axis)) / axisDelta;
            if (parameter > SIDE_EPSILON && parameter <= 1.0D + SIDE_EPSILON
                    && pointWithinFace(face, start.add(delta.scale(parameter)))) {
                parameters.add(parameter);
            }
        }
        parameters.sort(Double::compare);
        double parameterOffset = Math.min(0.25D, Math.max(CROSSING_EPSILON * 4.0D, 1.0E-4D / delta.length()));
        int crossings = 0;
        int index = 0;
        while (index < parameters.size()) {
            double parameter = parameters.get(index);
            int next = index + 1;
            while (next < parameters.size() && parameters.get(next) - parameter <= CROSSING_EPSILON) {
                next++;
            }
            Vec3 before = start.lerp(end, parameter - parameterOffset);
            Vec3 after = start.lerp(end, parameter + parameterOffset);
            if (classify(view, before) != classify(view, after)) {
                crossings++;
            }
            index = next;
        }
        return crossings;
    }

    private static double distanceToFaceSquared(ProjectionSurface.Face face, Vec3 point) {
        BlockPos position = face.position();
        double x = Mth.clamp(point.x, position.getX(), position.getX() + 1.0D);
        double y = Mth.clamp(point.y, position.getY(), position.getY() + 1.0D);
        double z = Mth.clamp(point.z, position.getZ(), position.getZ() + 1.0D);
        double plane = facePlane(face);
        switch (face.normal().getAxis()) {
            case X -> x = plane;
            case Y -> y = plane;
            case Z -> z = plane;
        }
        return point.distanceToSqr(x, y, z);
    }

    private static double signedFaceDistance(ProjectionSurface.Face face, Vec3 point) {
        return (point.get(face.normal().getAxis()) - facePlane(face)) * face.normal().getAxisDirection().getStep();
    }

    private static boolean pointWithinFace(ProjectionSurface.Face face, Vec3 point) {
        BlockPos position = face.position();
        return switch (face.normal().getAxis()) {
            case X -> withinFace(point.y, position.getY()) && withinFace(point.z, position.getZ());
            case Y -> withinFace(point.x, position.getX()) && withinFace(point.z, position.getZ());
            case Z -> withinFace(point.x, position.getX()) && withinFace(point.y, position.getY());
        };
    }

    private static boolean withinFace(double value, int minimum) {
        return value >= minimum - SIDE_EPSILON && value <= minimum + 1.0D + SIDE_EPSILON;
    }

    private static double facePlane(ProjectionSurface.Face face) {
        int coordinate = face.position().get(face.normal().getAxis());
        return coordinate + (face.normal().getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D : 0.0D);
    }

    private static boolean nearInteger(double value) {
        return Math.abs(value - Math.rint(value)) <= SIDE_EPSILON;
    }

    private static boolean segmentIntersectsBounds(AABB bounds, Vec3 start, Vec3 end) {
        return bounds.contains(start) || bounds.contains(end) || bounds.clip(start, end).isPresent();
    }
}
