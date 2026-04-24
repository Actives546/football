import request from '@/utils/request'

export function getSeasonById(id) {
  return request({
    url: `/season/${id}`,
    method: 'get'
  })
}

export function getSeasonByMatchId(matchId) {
  return request({
    url: `/season/match/${matchId}`,
    method: 'get'
  })
}

export function getSeasonPage(data) {
  return request({
    url: '/season/page',
    method: 'post',
    data
  })
}

export function addSeason(data) {
  return request({
    url: '/season',
    method: 'post',
    data
  })
}

export function updateSeason(data) {
  return request({
    url: '/season',
    method: 'put',
    data
  })
}

export function deleteSeason(id) {
  return request({
    url: `/season/${id}`,
    method: 'delete'
  })
}

export function deleteSeasonBatch(ids) {
  return request({
    url: '/season/batch',
    method: 'delete',
    data: ids
  })
}
