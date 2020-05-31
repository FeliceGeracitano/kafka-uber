export const RIDER_INIT_LOCATION = { lat: 41.888538, lon: 12.490261 } // Colosseum
export const TRIP_DESTINATION = { lat: 41.90101, lon: 12.482896 } // Trevi Fountain
export const TAXI_INIT_LOCATION = { lat: 41.890389, lon: 12.494072 } // Around Colosseum
export const noop = () => {}

export const CAMERA = {
  CENTER: 'CENTER',
  BACK: 'BACK'
}

export const TRIPS = [
  // // ROME
  // {
  //   rider: { lat: 41.888538, lon: 12.490261 },
  //   driver: { lat: 41.890389, lon: 12.494072 },
  //   destination: { lat: 41.90101, lon: 12.482896 }
  // },
  // // MILAN
  // {
  //   rider: { lat: 45.456044, lon: 9.18735 },
  //   driver: { lat: 45.45296, lon: 9.178091 },
  //   destination: { lat: 45.465155, lon: 9.188582 }
  // },
  // LONDON
  {
    rider: { lat: 51.505908, lon: -0.130722 },
    driver: { lat: 51.50649, lon: -0.129339 },
    destination: { lat: 51.505092, lon: -0.132762 }
  }
]

export const RANDOM_TRIP = TRIPS[Math.floor(Math.random() * TRIPS.length)]
