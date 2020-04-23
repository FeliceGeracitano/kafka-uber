<script>
  import { getContext, afterUpdate } from 'svelte'
  import { mapbox, mapContextKey } from '../../utils/mapbox'
  import { onMount } from 'svelte'

  export let lat
  export let lon
  export let icon // from | to | destination
  let marker
  let markerDOM

  const { getMap } = getContext(mapContextKey)

  // onMount(() => {
  //   marker = new mapbox.Marker()
  // })

  // afterUpdate(() => {
  //   if (!lat || !lon) return
  //   const map = getMap()
  //   maker.setLngLat([lon, lat])
  //   marker.addTo(map)
  // })

  afterUpdate(() => {
    if (!lat || !lon) return
    const map = getMap()
    new mapbox.Marker(markerDOM).setLngLat([lon, lat]).addTo(map)
  })
</script>

<style>
  .icon {
    width: 22px;
    height: 22px;
  }

  .to {
    background: url(/images/flag-checkered-solid.svg);
    background-color: white;
    box-shadow: 0px 0px 0px 5px white;
    box-shadow: 0px 0px 0px 6px rgba(0, 0, 0, 0.06);
    border-radius: 4px;
    border: 3px solid white;
  }
  .driver {
    background: url(/images/car-solid.svg);
    background-color: white;
    box-shadow: 0px 0px 0px 5px white;
    box-shadow: 0px 0px 0px 6px rgba(0, 0, 0, 0.06);
    border-radius: 4px;
    border: 3px solid white;
    background-repeat: no-repeat;
    background-size: contain;
    background-position: center;
    background-size: auto;
  }

  .rider {
    background: url(/images/male-solid.svg);
    background-color: white;
    box-shadow: 0px 0px 0px 5px white;
    box-shadow: 0px 0px 0px 6px rgba(0, 0, 0, 0.06);
    border-radius: 4px;
    border: 3px solid white;
    background-repeat: no-repeat;
    background-size: contain;
    background-position: center;
    background-size: auto;
  }

  .current-location {
    background-color: #007eff;
    background-size: cover;
    border-radius: 50%;
    cursor: pointer;
    border: 3px solid white;
    animation: current-location 1s ease-in-out 0s infinite alternate-reverse none;
  }

  @keyframes current-location {
    0% {
      box-shadow: 0px 0px 7px 5px transparent;
    }
    100% {
      box-shadow: 0px 0px 7px 5px #007eff87;
    }
  }
</style>

<div class={'icon ' + icon} bind:this={markerDOM} />
