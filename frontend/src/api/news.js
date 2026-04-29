import request from '@/utils/request'

export function getNewsById(id) {
  return request({
    url: `/news/${id}`,
    method: 'get'
  })
}

export function getNewsPage(data) {
  return request({
    url: '/news/page',
    method: 'post',
    data
  })
}

export function addNews(data) {
  return request({
    url: '/news',
    method: 'post',
    data
  })
}

export function updateNews(data) {
  return request({
    url: '/news',
    method: 'put',
    data
  })
}

export function deleteNews(id) {
  return request({
    url: `/news/${id}`,
    method: 'delete'
  })
}

export function deleteNewsBatch(ids) {
  return request({
    url: '/news/batch',
    method: 'delete',
    data: ids
  })
}

export function updateNewsStatus(id, status) {
  return request({
    url: `/news/status/${id}`,
    method: 'put',
    params: { status }
  })
}

export function updateNewsStatusBatch(ids, status) {
  return request({
    url: '/news/status/batch',
    method: 'put',
    params: { status },
    data: ids
  })
}
