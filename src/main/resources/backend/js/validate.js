function isValidUsername (str) {
  return ['admin', 'editor'].indexOf(str.trim()) >= 0;
}

function isExternal (path) {
  return /^(https?:|mailto:|tel:)/.test(path);
}

function isCellPhone (val) {
  if (!/^1(3|4|5|6|7|8)\d{9}$/.test(val)) {
    return false;
  } else {
    return true;
  }
}

// Validate username
function checkUserName (rule, value, callback) {
  if (value == "") {
    callback(new Error("Please enter a username"));
  } else if (value.length > 20 || value.length < 3) {
    callback(new Error("Username length should be 3-20 characters"));
  } else {
    callback();
  }
}

// Validate name
function checkName (rule, value, callback) {
  if (value == "") {
    callback(new Error("Please enter a name"));
  } else if (value.length > 12) {
    callback(new Error("Name length should be 1-12 characters"));
  } else {
    callback();
  }
}

function checkPhone (rule, value, callback) {
  if (value == "") {
    callback(new Error("Please enter a phone number"));
  } else if (!isCellPhone(value)) { // Use the method for checking phone format from methods
    callback(new Error("Please enter a valid phone number!"));
  } else {
    callback();
  }
}

function validID (rule, value, callback) {
  // ID card number is 15 or 18 digits long; 15 digits are all numbers, 18 digits are numbers for the first 17, and the last one can be a number or the character X
  let reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
  if (value == '') {
    callback(new Error('Please enter an ID card number'));
  } else if (reg.test(value)) {
    callback();
  } else {
    callback(new Error('Invalid ID card number'));
  }
}
