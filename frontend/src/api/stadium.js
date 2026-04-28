import request from '@/utils/request'

export function getStadiumById(id) {
  return request({
    url: `/stadium/${id}`,
    method: 'get'
  })
}

export function getStadiumAll() {
  return request({
    url: '/stadium/all',
    method: 'get'
  })
}

export function getStadiumEnabled() {
  return request({
    url: '/stadium/enabled',
    method: 'get'
  })
}

export function getStadiumPage(data) {
  return request({
    url: '/stadium/page',
    method: 'post',
    data
  })
}

export function addStadium(data) {
  return request({
    url: '/stadium',
    method: 'post',
    data
  })
}

export function updateStadium(data) {
  return request({
    url: '/stadium',
    method: 'put',
    data
  })
}

export function deleteStadium(id) {
  return request({
    url: `/stadium/${id}`,
    method: 'delete'
  })
}

export function deleteStadiumBatch(ids) {
  return request({
    url: '/stadium/batch',
    method: 'delete',
    data: ids
  })
}

export function enableStadiumBatch(ids) {
  return request({
    url: '/stadium/enable',
    method: 'post',
    data: ids
  })
}

export function disableStadiumBatch(ids) {
  return request({
    url: '/stadium/disable',
    method: 'post',
    data: ids
  })
}
