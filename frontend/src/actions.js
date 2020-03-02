export const ACTION_TYPE = {
  SYNC_STATUS: "SYNC_STATUS",
  REQUEST_TRIP: "REQUEST_TRIP",
  CONFIRM_TRIP: "CONFIRM_TRIP",
  START_TRIP: "START_TRIP",
  END_TRIP: "END_TRIP",
  UPDATE_DRIVER_LOCATION: "UPDATE_DRIVER_LOCATION"
};

export default {
  rider: {
    requestTrip: (from, to) =>
      JSON.stringify({
        type: ACTION_TYPE.REQUEST_TRIP,
        payload: JSON.stringify({ destination: from, riderLocation: to })
      })
  },
  driver: {
    confirmTrip: (tripId, driverLocation) =>
      JSON.stringify({
        type: ACTION_TYPE.CONFIRM_TRIP,
        payload: JSON.stringify({ tripId, driverLocation })
      }),
    updateLocation: driverLocation =>
      JSON.stringify({
        type: ACTION_TYPE.UPDATE_DRIVER_LOCATION,
        payload: JSON.stringify(driverLocation)
      }),
    startTrip: () =>
      JSON.stringify({ type: ACTION_TYPE.START_TRIP, payload: null }),
    endTrip: () => JSON.stringify({ type: ACTION_TYPE.END_TRIP, payload: null })
  }
};
