export const ACTION_TYPE = {
  CONFIRM_TRIP: 'CONFIRM_TRIP',
  END_TRIP: 'END_TRIP',
  REQUEST_TRIP: 'REQUEST_TRIP',
  START_TRIP: 'START_TRIP',
  SYNC_STATUS: 'SYNC_STATUS',
  UPDATE_DRIVER_LOCATION: 'UPDATE_DRIVER_LOCATION'
}

export default {
  rider: {
    requestTrip: ({ from, to }) => ({
      type: ACTION_TYPE.REQUEST_TRIP,
      payload: JSON.stringify({ riderLocation: from, destination: to })
    })
  },
  driver: {
    confirmTrip: (tripId, driverLocation) => ({
      type: ACTION_TYPE.CONFIRM_TRIP,
      payload: JSON.stringify({ tripId, driverLocation })
    }),
    updateLocation: (driverLocation) => ({
      type: ACTION_TYPE.UPDATE_DRIVER_LOCATION,
      payload: JSON.stringify(driverLocation)
    }),
    startTrip: () => ({ type: ACTION_TYPE.START_TRIP, payload: null }),
    endTrip: (tripId, amount, distance) => ({
      type: ACTION_TYPE.END_TRIP,
      payload: JSON.stringify({ tripId, amount, distance })
    })
  }
}
