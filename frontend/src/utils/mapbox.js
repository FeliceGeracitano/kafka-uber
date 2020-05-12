import mapbox from 'mapbox-gl'
import 'mapbox-gl/dist/mapbox-gl.css'

mapbox.accessToken =
  'pk.eyJ1IjoiZmVsaWNlZ2VyYWNpdGFubyIsImEiOiJjanhtMDJpMWYwMno2M29vNDJmbDZoZ2NjIn0.wi38HXu-VVmUkJkkqs3zVA'

const mapContextKey = 'mapContextKey'
export { mapbox, mapContextKey }

export async function getDirections(...locations) {
  const coordinatesString = locations.map(({ lon, lat }) => `${lon},${lat}`).join(';')
  const endpoint = `https://api.mapbox.com/directions/v5/mapbox/driving/${coordinatesString}?geometries=geojson&access_token=${mapbox.accessToken}`
  return await (await fetch(endpoint)).json()
}
