package nerd.tuxmobil.fahrplan.congress.dataconverters

import info.metadude.android.eventfahrplan.commons.temporal.Moment
import info.metadude.android.eventfahrplan.database.models.Highlight
import info.metadude.android.eventfahrplan.database.models.Lecture.Companion.RECORDING_OPT_OUT_ON
import nerd.tuxmobil.fahrplan.congress.models.DateInfo
import nerd.tuxmobil.fahrplan.congress.models.Lecture
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import info.metadude.android.eventfahrplan.database.models.Lecture as LectureDatabaseModel
import info.metadude.android.eventfahrplan.network.models.Lecture as LectureNetworkModel

class LectureExtensionsTest {

    @Test
    fun lectureDatabaseModel_toLectureAppModel_toLectureDatabaseModel() {
        val lecture = LectureDatabaseModel(
                eventId = "7331",
                abstractt = "Lorem ipsum",
                dayIndex = 3,
                date = "2015-08-13",
                dateUTC = 1439478900000L,
                description = "Lorem ipsum dolor sit amet",
                duration = 45,
                hasAlarm = true,
                isHighlight = true,
                language = "en",
                links = "[Website](https://www.example.com/path)",
                relativeStartTime = 1035,
                recordingLicense = "CC 0",
                recordingOptOut = RECORDING_OPT_OUT_ON,
                room = "Simulacron-3",
                roomIndex = 17,
                speakers = "John Doe; Noah Doe",
                startTime = 1036,
                slug = "lorem",
                subtitle = "My subtitle",
                title = "My title",
                track = "Security & Hacking",
                type = "tutorial",
                url = "https://talks.mrmcd.net/2018/talk/V3FUNG",

                changedDay = true,
                changedDuration = true,
                changedIsCanceled = true,
                changedIsNew = true,
                changedLanguage = true,
                changedRecordingOptOut = true,
                changedRoom = true,
                changedSpeakers = true,
                changedSubtitle = true,
                changedTime = true,
                changedTitle = true,
                changedTrack = true
        )
        assertThat(lecture.toLectureAppModel().toLectureDatabaseModel()).isEqualTo(lecture)
    }

    @Test
    fun lectureNetworkModel_toLectureAppModel_toLectureNetworkModel() {
        val lecture = LectureNetworkModel(
                eventId = "7331",
                abstractt = "Lorem ipsum",
                dayIndex = 3,
                date = "2015-08-13",
                dateUTC = 1439478900000L,
                description = "Lorem ipsum dolor sit amet",
                duration = 45,
                hasAlarm = true,
                isHighlight = true,
                language = "en",
                links = "[Website](https://www.example.com/path)",
                relativeStartTime = 1035,
                recordingLicense = "CC 0",
                recordingOptOut = RECORDING_OPT_OUT_ON,
                room = "Simulacron-3",
                roomIndex = 17,
                speakers = "John Doe; Noah Doe",
                startTime = 1036,
                slug = "lorem",
                subtitle = "My subtitle",
                title = "My title",
                track = "Security & Hacking",
                type = "tutorial",
                url = "https://talks.mrmcd.net/2018/talk/V3FUNG",

                changedDayIndex = true,
                changedDuration = true,
                changedIsCanceled = true,
                changedIsNew = true,
                changedLanguage = true,
                changedRecordingOptOut = true,
                changedRoom = true,
                changedSpeakers = true,
                changedSubtitle = true,
                changedStartTime = true,
                changedTitle = true,
                changedTrack = true
        )
        assertThat(lecture.toLectureAppModel().toLectureNetworkModel()).isEqualTo(lecture)
    }

    @Test
    fun toDateInfo() {
        val lecture = Lecture("")
        lecture.date = "2015-08-13"
        lecture.day = 3
        val dateInfo = DateInfo(3, Moment("2015-08-13"))
        assertThat(lecture.toDateInfo()).isEqualTo(dateInfo)
    }

    @Test
    fun toDayRanges() {
        val lecture0 = Lecture("")
        lecture0.date = "2019-08-02"
        lecture0.day = 2

        val lecture1 = Lecture("")
        lecture1.date = "2019-08-01"
        lecture1.day = 1

        val lecture1Copy = Lecture("")
        lecture1Copy.date = "2019-08-01"
        lecture1Copy.day = 1

        val lectures = listOf(lecture0, lecture1, lecture1Copy)
        val dayRanges = lectures.toDayRanges()

        assertThat(dayRanges.size).isEqualTo(2)
        assertThat(dayRanges[0].startsAt.dayOfMonth).isEqualTo(1)
        assertThat(dayRanges[0].startsAt.hour).isEqualTo(0)
        assertThat(dayRanges[1].startsAt.dayOfMonth).isEqualTo(2)
        assertThat(dayRanges[1].startsAt.hour).isEqualTo(0)

        assertThat(dayRanges[0].endsAt.dayOfMonth).isEqualTo(1)
        assertThat(dayRanges[0].endsAt.hour).isEqualTo(23)
        assertThat(dayRanges[1].endsAt.dayOfMonth).isEqualTo(2)
        assertThat(dayRanges[1].endsAt.hour).isEqualTo(23)
    }

    @Test
    fun toHighlightDatabaseModel() {
        val lecture = Lecture("")
        lecture.lectureId = "4723"
        lecture.highlight = true
        val highlight = Highlight(eventId = 4723, isHighlight = true)
        assertThat(lecture.toHighlightDatabaseModel()).isEqualTo(highlight)
    }

    @Test
    fun sanitizeWithSameTitleAndSubtitle() {
        val lecture = Lecture("").apply {
            subtitle = "Lorem ipsum"
            title = "Lorem ipsum"
        }.sanitize()
        val expected = Lecture("").apply {
            subtitle = ""
            title = "Lorem ipsum"
        }
        assertThat(lecture).isEqualTo(expected)
    }

    @Test
    fun sanitizeWithDifferentTitleAndSubtitle() {
        val lecture = Lecture("").apply {
            subtitle = "Dolor sit amet"
            title = "Lorem ipsum"
        }.sanitize()
        val expected = Lecture("").apply {
            subtitle = "Dolor sit amet"
            title = "Lorem ipsum"
        }
        assertThat(lecture).isEqualTo(expected)
    }

    @Test
    fun sanitizeWithSameAbstractAndDescription() {
        val lecture = Lecture("").apply {
            abstractt = "Lorem ipsum"
            description = "Lorem ipsum"
        }.sanitize()
        val expected = Lecture("").apply {
            abstractt = ""
            description = "Lorem ipsum"
        }
        // The "abstractt" and "description" fields are not part of Lecture#equals for some reason.
        assertThat(lecture.abstractt).isEqualTo(expected.abstractt)
        assertThat(lecture.description).isEqualTo(expected.description)
    }

    @Test
    fun sanitizeWithDifferentAbstractAndDescription() {
        val lecture = Lecture("").apply {
            abstractt = "Lorem ipsum"
            description = "Dolor sit amet"
        }.sanitize()
        val expected = Lecture("").apply {
            abstractt = "Lorem ipsum"
            description = "Dolor sit amet"
        }
        // The "abstractt" and "description" fields are not part of Lecture#equals for some reason.
        assertThat(lecture.abstractt).isEqualTo(expected.abstractt)
        assertThat(lecture.description).isEqualTo(expected.description)
    }

    @Test
    fun sanitizeWithAbstractWithoutDescription() {
        val lecture = Lecture("").apply {
            abstractt = "Lorem ipsum"
            description = ""
        }.sanitize()
        val expected = Lecture("").apply {
            description = "Lorem ipsum"
        }
        // The "abstractt" and "description" fields are not part of Lecture#equals for some reason.
        assertThat(lecture.abstractt).isEqualTo(expected.abstractt)
        assertThat(lecture.description).isEqualTo(expected.description)
    }

    @Test
    fun sanitizeWithSameSpeakersAndSubtitle() {
        val lecture = Lecture("").apply {
            speakers = "Luke Skywalker"
            subtitle = "Luke Skywalker"
        }.sanitize()
        val expected = Lecture("").apply {
            speakers = "Luke Skywalker"
            subtitle = ""
        }
        assertThat(lecture).isEqualTo(expected)
    }

    @Test
    fun sanitizeWithDifferentSpeakersAndAbstract() {
        val lecture = Lecture("").apply {
            speakers = "Darth Vader"
            subtitle = "Lorem ipsum"
        }.sanitize()
        val expected = Lecture("").apply {
            speakers = "Darth Vader"
            subtitle = "Lorem ipsum"
        }
        assertThat(lecture).isEqualTo(expected)
    }

}
