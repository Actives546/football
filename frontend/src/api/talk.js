import request from '@/utils/request'

export function getTalkById(id) {
  return request({
    url: `/news/${id}`,
    method: 'get'
  })
}

export function getTalkPage(data) {
  return request({
    url: '/news/page',
    method: 'post',
    data: { ...data, category: '说说' }
  })
}

export function addTalk(data) {
  return request({
    url: '/news',
    method: 'post',
    data: { ...data, category: '说说' }
  })
}

export function updateTalk(data) {
  return request({
    url: '/news',
    method: 'put',
    data: { ...data, category: '说说' }
  })
}

export function deleteTalk(id) {
  return request({
    url: `/news/${id}`,
    method: 'delete'
  })
}

export function deleteTalkBatch(ids) {
  return request({
    url: '/news/batch',
    method: 'delete',
    data: ids
  })
}

export function updateTalkStatus(id, status) {
  return request({
    url: `/news/status/${id}`,
    method: 'put',
    params: { status }
  })
}

export function updateTalkStatusBatch(ids, status) {
  return request({
    url: '/news/status/batch',
    method: 'put',
    params: { status },
    data: ids
  })
}
