import request from '@/utils/request'

export function getPersonById(id) {
  return request({
    url: `/person/${id}`,
    method: 'get'
  })
}

export function getPersonPage(data) {
  return request({
    url: '/person/page',
    method: 'post',
    data
  })
}

export function getPersonByOrgId(orgId) {
  return request({
    url: `/person/org/${orgId}`,
    method: 'get'
  })
}

export function addPerson(data) {
  return request({
    url: '/person',
    method: 'post',
    data
  })
}

export function updatePerson(data) {
  return request({
    url: '/person',
    method: 'put',
    data
  })
}

export function deletePerson(id) {
  return request({
    url: `/person/${id}`,
    method: 'delete'
  })
}

export function deletePersonBatch(ids) {
  return request({
    url: '/person/batch',
    method: 'delete',
    data: ids
  })
}

export function getPersonDetailById(id) {
  return request({
    url: `/person/detail/${id}`,
    method: 'get'
  })
}

export function savePersonDetail(data) {
  return request({
    url: '/person/detail',
    method: 'post',
    data
  })
}
