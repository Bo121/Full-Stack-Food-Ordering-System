// Get member list
function getMemberList(params) {
  return $axios({
    url: '/employee/page',
    method: 'get',
    params
  })
}

// Modify - Enable or disable employee
function enableOrDisableEmployee(params) {
  return $axios({
    url: '/employee',
    method: 'put',
    data: { ...params }
  })
}

// Add - Add employee
function addEmployee(params) {
  return $axios({
    url: '/employee',
    method: 'post',
    data: { ...params }
  })
}

// Edit - Add employee (This comment seems to be a mistake, it should be "Edit employee")
function editEmployee(params) {
  return $axios({
    url: '/employee',
    method: 'put',
    data: { ...params }
  })
}

// Query employee details for edit page
function queryEmployeeById(id) {
  return $axios({
    url: `/employee/${id}`,
    method: 'get'
  })
}
