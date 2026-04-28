import request from '@/utils/request'

export function getTeamById(id) {
  return request({
    url: `/team/${id}`,
    method: 'get'
  })
}

export function getTeamAll() {
  return request({
    url: '/team/all',
    method: 'get'
  })
}

export function getTeamPage(data) {
  return request({
    url: '/team/page',
    method: 'post',
    data
  })
}

export function addTeam(data) {
  return request({
    url: '/team',
    method: 'post',
    data
  })
}

export function updateTeam(data) {
  return request({
    url: '/team',
    method: 'put',
    data
  })
}

export function deleteTeam(id) {
  return request({
    url: `/team/${id}`,
    method: 'delete'
  })
}

export function deleteTeamBatch(ids) {
  return request({
    url: '/team/batch',
    method: 'delete',
    data: ids
  })
}

export function exportTeam(data) {
  return request({
    url: '/team/export',
    method: 'post',
    data
  })
}

export function enableTeamBatch(ids) {
  return request({
    url: '/team/enable',
    method: 'post',
    data: ids
  })
}

export function disableTeamBatch(ids) {
  return request({
    url: '/team/disable',
    method: 'post',
    data: ids
  })
}
