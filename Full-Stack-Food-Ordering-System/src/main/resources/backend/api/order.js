// Query order detail list page interface
const getOrderDetailPage = (params) => {
  return $axios({
    url: '/order/page',
    method: 'get',
    params
  })
}

// View order detail by ID
const queryOrderDetailById = (id) => {
  return $axios({
    url: `/orderDetail/${id}`,
    method: 'get'
  })
}

// Cancel, deliver, complete order interface
const editOrderDetail = (params) => {
  return $axios({
    url: '/order',
    method: 'put',
    data: { ...params }
  })
}
