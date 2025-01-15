// Query list interface
const getDishPage = (params) => {
  return $axios({
    url: '/dish/page',
    method: 'get',
    params
  })
}

// Delete interface
const deleteDish = (ids) => {
  return $axios({
    url: '/dish',
    method: 'delete',
    params: { ids }
  })
}

// Edit interface
const editDish = (params) => {
  return $axios({
    url: '/dish',
    method: 'put',
    data: { ...params }
  })
}

// Add new interface
const addDish = (params) => {
  return $axios({
    url: '/dish',
    method: 'post',
    data: { ...params }
  })
}

// Query details
const queryDishById = (id) => {
  return $axios({
    url: `/dish/${id}`,
    method: 'get'
  })
}

// Get dish category list
const getCategoryList = (params) => {
  return $axios({
    url: '/category/list',
    method: 'get',
    params
  })
}

// Query dish list interface
const queryDishList = (params) => {
  return $axios({
    url: '/dish/list',
    method: 'get',
    params
  })
}

// File download preview
const commonDownload = (params) => {
  return $axios({
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    },
    url: '/common/download',
    method: 'get',
    params
  })
}

// Start/stop sale - batch start/stop sale interface
const dishStatusByStatus = (params) => {
  return $axios({
    url: `/dish/status/${params.status}`,
    method: 'post',
    params: { ids: params.id }
  })
}
