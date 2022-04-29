package com.luispalenciadelcampo.travelink.domain.usecases

import com.luispalenciadelcampo.travelink.domain.usecases.auth.LoginGoogleUseCase
import com.luispalenciadelcampo.travelink.domain.usecases.auth.LoginUseCase
import com.luispalenciadelcampo.travelink.domain.usecases.auth.RegisterUserUseCase
import com.luispalenciadelcampo.travelink.domain.usecases.auth.SendRecoverPasswordEmailUseCase
import com.luispalenciadelcampo.travelink.domain.usecases.trips.*

class UseCase (
    var RegisterUserUseCase: RegisterUserUseCase,
    var LoginUseCase: LoginUseCase,
    var LoginGoogleUseCase: LoginGoogleUseCase,
    var SendRecoverPasswordEmailUseCase: SendRecoverPasswordEmailUseCase,
    var GetUserInfoUseCase: GetUserInfoUseCase,
    var GetTripsUseCase: GetTripsUseCase,
    var CreateTripUseCase: CreateTripUseCase,
    var CreateEventUseCase: CreateEventUseCase,
    var GetEventsUseCase: GetEventsUseCase,
    var RemoveEventUseCase: RemoveEventUseCase,
    var RemoveTripUseCase: RemoveTripUseCase,
    var RateTrip: RateTrip,
    var GetPlaceImageUseCase: GetPlaceImageUseCase,
    var GetEventPhotoUseCase: GetEventPhotoUseCase,
    var GetTripPhotoUseCase: GetTripPhotoUseCase,
    var UpdateEventUseCase: UpdateEventUseCase,
    var UpdateUserInfoUseCase: UpdateUserInfoUseCase,
    var UploadProfileImageUseCase: UploadProfileImageUseCase,
    var RateEventUseCase: RateEventUseCase
)