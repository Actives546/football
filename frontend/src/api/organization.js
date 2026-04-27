import request from '@/utils/request'

export function getOrganizationById(id) {
  return request({
    url: `/organization/${id}`,
    method: 'get'
  })
}

export function getOrganizationTree() {
  return request({
    url: '/organization/tree',
    method: 'get'
  })
}

export function getOrganizationChildren(parentId) {
  return request({
    url: `/organization/children/${parentId || 0}`,
    method: 'get'
  })
}

export function getOrganizationAll() {
  return request({
    url: '/organization/all',
    method: 'get'
  })
}

export function getOrganizationPage(data) {
  return request({
    url: '/organization/page',
    method: 'post',
    data
  })
}

export function addOrganization(data) {
  return request({
    url: '/organization',
    method: 'post',
    data
  })
}

export function updateOrganization(data) {
  return request({
    url: '/organization',
    method: 'put',
    data
  })
}

export function deleteOrganization(id) {
  return request({
    url: `/organization/${id}`,
    method: 'delete'
  })
}

export function deleteOrganizationBatch(ids) {
  return request({
    url: '/organization/batch',
    method: 'delete',
    data: ids
  })
}
