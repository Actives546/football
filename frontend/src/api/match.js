import request from '@/utils/request'

export function getMatchById(id) {
  return request({
    url: `/match/${id}`,
    method: 'get'
  })
}

export function getMatchPage(data) {
  return request({
    url: '/match/page',
    method: 'post',
    data
  })
}

export function addMatch(data) {
  return request({
    url: '/match',
    method: 'post',
    data
  })
}

export function updateMatch(data) {
  return request({
    url: '/match',
    method: 'put',
    data
  })
}

export function deleteMatch(id) {
  return request({
    url: `/match/${id}`,
    method: 'delete'
  })
}

export function deleteMatchBatch(ids) {
  return request({
    url: '/match/batch',
    method: 'delete',
    data: ids
  })
}
