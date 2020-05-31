// https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
function uuidv4() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    var r = (Math.random() * 16) | 0,
      v = c == 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

// key = 'DRIVER' | 'RIDER'
let uuid
export const getUid = (key) => {
  uuid = uuid ? uuid : uuidv4()
  // if (!localStorage.getItem("UID")) localStorage.setItem("UID", uuidv4());
  return `${key.charAt(0)}${uuid}`
}

// Form '100' to '01: 40'
export const formatSeconds = (seconds) =>
  `${String(Math.floor(seconds / 60)).padStart(2, '0')}:${String(Math.floor(seconds % 60)).padStart(2, '0')}`

export const getTripAmount = () => {
  const MIN = 10
  const MAX = 50
  return (Math.random() * (MAX - MIN) + MIN).toFixed(2)
}
