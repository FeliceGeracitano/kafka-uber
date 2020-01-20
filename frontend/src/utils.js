// https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
function uuidv4() {
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function(c) {
    var r = (Math.random() * 16) | 0,
      v = c == "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

// key = 'DRIVER' | 'RIDER'
export const getUid = (key = "") => {
  const UidFromStorage = localStorage.getItem(key);
  if (UidFromStorage) return UidFromStorage;
  const uuid = `D${uuidv4()}`;
  localStorage.setItem(key, uuid);
  return uuid;
};
