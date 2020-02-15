import mapbox from "mapbox-gl";
import "mapbox-gl/dist/mapbox-gl.css";
mapbox.accessToken =
  "pk.eyJ1IjoiZmVsaWNlZ2VyYWNpdGFubyIsImEiOiJjanhtMDJpMWYwMno2M29vNDJmbDZoZ2NjIn0.wi38HXu-VVmUkJkkqs3zVA";

const mapContextKey = "mapContextKey";
export { mapbox, mapContextKey };
