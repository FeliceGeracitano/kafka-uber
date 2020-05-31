export const RIDER_INIT_LOCATION = { lat: 41.888538, lon: 12.490261 } // Colosseum
export const TRIP_DESTINATION = { lat: 41.90101, lon: 12.482896 } // Trevi Fountain
export const TAXI_INIT_LOCATION = { lat: 41.890389, lon: 12.494072 } // Around Colosseum
export const noop = () => {}

export const CAMERA = {
  CENTER: 'CENTER',
  BACK: 'BACK'
}

export const TRIPS = [
  // ROMA
  {
    driver: { lat: 41.890389, lon: 12.494072 },
    rider: { lat: 41.888538, lon: 12.490261 },
    destination: { lat: 41.90101, lon: 12.482896 }
  },
  // MILAN
  {
    driver: { lat: 45.45296, lon: 9.178091 },
    rider: { lat: 45.456044, lon: 9.18735 },
    destination: { lat: 45.465155, lon: 9.188582 }
  },
  // LONDON
  {
    driver: { lat: 51.501078, lon: -0.126236 },
    rider: { lat: 51.50714, lon: -0.12793 },
    destination: { lat: 51.50222, lon: -0.141114 }
  },
  // NY
  {
    driver: { lat: 40.7505637, lon: -73.9932548 },
    rider: { lat: 40.752413, lon: -73.985836 },
    destination: { lat: 40.7562563, lon: -73.9834948 }
  },
  // TOKYO
  {
    driver: { lat: 35.6637248, lon: 139.7813928 },
    rider: { lat: 35.664697, lon: 139.775353 },
    destination: { lat: 35.661751, lon: 139.7747294 }
  },
  // SF
  {
    driver: { lat: 37.754795, lon: -122.441818 },
    rider: { lat: 37.76198, lon: -122.437095 },
    destination: { lat: 37.768388, lon: -122.427653 }
  },
  // SYDNEY
  {
    driver: { lat: -33.864257, lon: 151.205528 },
    rider: { lat: -33.866696, lon: 151.209974 },
    destination: { lat: -33.871041, lon: 151.210174 }
  }
]

export const RANDOM_TRIP = TRIPS[Math.floor(Math.random() * TRIPS.length)]
