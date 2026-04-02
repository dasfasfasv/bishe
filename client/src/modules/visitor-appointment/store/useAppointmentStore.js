import { defineStore } from 'pinia'
import {
  fetchAppointments,
  fetchAppointmentById,
  createAppointment,
  updateAppointment,
  patchAppointmentStatus,
  deleteAppointment
} from '../api/appointment'

export const useAppointmentStore = defineStore('appointment', {
  state: () => ({
    list: [],
    current: null,
    loading: false,
    error: null
  }),
  actions: {
    async loadList() {
      this.loading = true
      this.error = null
      try {
        this.list = await fetchAppointments()
      } catch (e) {
        this.error = e.message
      } finally {
        this.loading = false
      }
    },
    async loadById(id) {
      this.loading = true
      this.error = null
      try {
        this.current = await fetchAppointmentById(id)
      } catch (e) {
        this.error = e.message
      } finally {
        this.loading = false
      }
    },
    async create(payload) {
      this.loading = true
      this.error = null
      try {
        const record = await createAppointment(payload)
        this.list.unshift(record)
        return record
      } catch (e) {
        this.error = e.message
        throw e
      } finally {
        this.loading = false
      }
    },
    async update(id, payload) {
      this.loading = true
      this.error = null
      try {
        const record = await updateAppointment(id, payload)
        const idx = this.list.findIndex(item => item.id === Number(id))
        if (idx !== -1) {
          this.list[idx] = record
        }
        if (this.current && this.current.id === Number(id)) {
          this.current = record
        }
        return record
      } catch (e) {
        this.error = e.message
        throw e
      } finally {
        this.loading = false
      }
    },
    async changeStatus(id, status, remark = '') {
      this.loading = true
      this.error = null
      try {
        const record = await patchAppointmentStatus(id, status, remark)
        const idx = this.list.findIndex(item => item.id === Number(id))
        if (idx !== -1) {
          this.list[idx] = record
        }
        if (this.current && this.current.id === Number(id)) {
          this.current = record
        }
        return record
      } catch (e) {
        this.error = e.message
        throw e
      } finally {
        this.loading = false
      }
    },
    async remove(id) {
      this.loading = true
      this.error = null
      try {
        await deleteAppointment(id)
        this.list = this.list.filter(item => item.id !== Number(id))
        if (this.current && this.current.id === Number(id)) {
          this.current = null
        }
        return true
      } catch (e) {
        this.error = e.message
        throw e
      } finally {
        this.loading = false
      }
    }
  }
})
