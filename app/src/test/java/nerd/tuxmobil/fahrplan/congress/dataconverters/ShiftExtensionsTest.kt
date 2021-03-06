package nerd.tuxmobil.fahrplan.congress.dataconverters

import info.metadude.android.eventfahrplan.commons.logging.Logging
import info.metadude.android.eventfahrplan.commons.temporal.DayRange
import info.metadude.android.eventfahrplan.commons.temporal.Moment
import info.metadude.kotlin.library.engelsystem.models.Shift
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

class ShiftExtensionsTest {

    @Test
    fun oneBasedDayIndex_DayOne() {
        val zoneOffset = ZoneOffset.ofHours(2)
        val day = Moment("2019-08-21")
        val dayRanges = listOf(DayRange(day))

        val shiftStart = day.toZonedDateTime(zoneOffset).plusSeconds(59)
        val shiftEnd = shiftStart.plusSeconds(59)
        val shift = Shift(startsAt = shiftStart, endsAt = shiftEnd)

        assertThat(shift.oneBasedDayIndex(NoLogging, dayRanges)).isEqualTo(1)
    }

    @Test
    fun oneBasedDayIndex_DayTwo() {
        val zoneOffset = ZoneOffset.ofHours(2)
        val day1 = Moment("2019-08-21")
        val day2 = Moment("2019-08-22")
        val dayRanges = listOf(DayRange(day1), DayRange(day2))

        val shiftStart = day2.toZonedDateTime(zoneOffset).plusSeconds(59)
        val shiftEnd = shiftStart.plusSeconds(59)
        val shift = Shift(startsAt = shiftStart, endsAt = shiftEnd)

        assertThat(shift.oneBasedDayIndex(NoLogging, dayRanges)).isEqualTo(2)
    }

    @Test
    fun descriptionTextWithEmptyShift() {
        assertThat(Shift().descriptionText).isEmpty()
    }

    @Test
    fun descriptionTextWithShiftWithLocationName() {
        assertThat(Shift(locationName = "Room 23").descriptionText).isEqualTo("Room 23")
    }

    @Test
    fun descriptionTextWithShiftWithLocationUrl() {
        assertThat(Shift(locationUrl = "https://example.com").descriptionText).isEqualTo("<a href=\"https://example.com\">https://example.com</a>")
    }

    @Test
    fun descriptionTextWithShiftWithLocationDescription() {
        assertThat(Shift(locationDescription = "The large green room.").descriptionText).isEqualTo("The large green room.")
    }

    @Test
    fun descriptionTextWithShiftWithUserComment() {
        assertThat(Shift(userComment = "Don't forget a warm jacket.").descriptionText).isEqualTo("<em>Don't forget a warm jacket.</em>")
    }

    @Test
    fun descriptionTextWithShiftWithAllFields() {
        val shift = Shift(
                locationName = "Room 42",
                locationUrl = "https://conference.org",
                locationDescription = "The small orange room.",
                userComment = "Take a bottle of water with you"
        )
        val text = "Room 42<br /><a href=\"https://conference.org\">https://conference.org</a><br /><br />The small orange room.<br /><br /><em>Take a bottle of water with you</em>"
        assertThat(shift.descriptionText).isEqualTo(text)
    }

    @Test
    fun toLectureAppModel_timeZoneHandling() {
        val day = Moment("2019-01-02")
        val startsAt = day.toZonedDateTime(ZoneOffset.ofHours(4)) // 2019-01-02 04:00 with offset +4 => still  2019-01-02 00:00Z
        val shift = Shift(
                startsAt = startsAt,
                timeZoneOffset = ZoneOffset.ofHours(2) // for whatever reason someone sets timeZoneOffset different than startsAts offset
        )
        val dayRange = DayRange(day)
        val lecture = shift.toLectureAppModel(NoLogging, "", listOf(dayRange))
        assertThat(lecture.startTime).isEqualTo(0) // nevertheless, we still expect lectures time data to be based on UTC
        assertThat(lecture.relStartTime).isEqualTo(0)
    }

    @Test
    fun getDurationMinutes() {
        val day = Moment("2019-08-25")
        val startsAtDate = ZonedDateTime.of(2019, 8, 25, 12, 0, 0, 0, ZoneOffset.UTC)
        val endsAtDate = ZonedDateTime.of(2019, 8, 25, 12, 30, 13, 0, ZoneOffset.UTC)
        val dayRange = DayRange(day)
        val shift = Shift(startsAt = startsAtDate, endsAt = endsAtDate)
        assertThat(shift.toLectureAppModel(NoLogging, "", listOf(dayRange)).duration).isEqualTo(30)
    }

    object NoLogging : Logging {
        override fun d(tag: String, message: String) = Unit
        override fun e(tag: String, message: String) = Unit
        override fun report(tag: String, message: String) = Unit
    }

}
