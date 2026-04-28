import request from '@/utils/request'

export function getScheduleById(id) {
  return request({
    url: `/schedule/${id}`,
    method: 'get'
  })
}

export function getScheduleBySeasonId(seasonId) {
  return request({
    url: `/schedule/season/${seasonId}`,
    method: 'get'
  })
}

export function getSchedulePage(data) {
  return request({
    url: '/schedule/page',
    method: 'post',
    data
  })
}

export function addSchedule(data) {
  return request({
    url: '/schedule',
    method: 'post',
    data
  })
}

export function updateSchedule(data) {
  return request({
    url: '/schedule',
    method: 'put',
    data
  })
}

export function deleteSchedule(id) {
  return request({
    url: `/schedule/${id}`,
    method: 'delete'
  })
}

export function deleteScheduleBatch(ids) {
  return request({
    url: '/schedule/batch',
    method: 'delete',
    data: ids
  })
}
