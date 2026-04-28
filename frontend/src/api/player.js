import request from '@/utils/request'

export function getPlayerById(id) {
  return request({
    url: `/person/${id}`,
    method: 'get'
  })
}

export function getPlayerPage(data) {
  return request({
    url: '/person/page',
    method: 'post',
    data
  })
}

export function getPlayerByOrgId(orgId) {
  return request({
    url: `/person/org/${orgId}`,
    method: 'get'
  })
}

export function addPlayer(data) {
  return request({
    url: '/person',
    method: 'post',
    data
  })
}

export function updatePlayer(data) {
  return request({
    url: '/person',
    method: 'put',
    data
  })
}

export function deletePlayer(id) {
  return request({
    url: `/person/${id}`,
    method: 'delete'
  })
}

export function deletePlayerBatch(ids) {
  return request({
    url: '/person/batch',
    method: 'delete',
    data: ids
  })
}
