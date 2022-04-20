package com.luispalenciadelcampo.travelink.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.repository.AuthRepositoryImpl
import com.luispalenciadelcampo.travelink.data.repository.MainRepositoryImpl
import com.luispalenciadelcampo.travelink.domain.repository.AuthRepository
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.domain.usecases.UseCase
import com.luispalenciadelcampo.travelink.domain.usecases.auth.LoginGoogleUseCase
import com.luispalenciadelcampo.travelink.domain.usecases.auth.LoginUseCase
import com.luispalenciadelcampo.travelink.domain.usecases.auth.RegisterUserUseCase
import com.luispalenciadelcampo.travelink.domain.usecases.auth.SendRecoverPasswordEmailUseCase
import com.luispalenciadelcampo.travelink.domain.usecases.trips.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TraveLinkModule {

    @Provides
    @Singleton
    fun provideRealTimeDb(): FirebaseDatabase {
        return FirebaseDatabase.getInstance(Constants.DB_URL)
    }

    @Provides
    @Singleton
    fun provideFirebaseAut(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepo(databaseReference: FirebaseDatabase, firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(databaseReference, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideMainRepo(databaseReference: FirebaseDatabase, firebaseAuth: FirebaseAuth): MainRepository {
        return MainRepositoryImpl(databaseReference, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideUseCases(authRepository: AuthRepository, mainRepository: MainRepository): UseCase {
        return UseCase(
            // Auth
            RegisterUserUseCase = RegisterUserUseCase(authRepository),
            LoginUseCase = LoginUseCase(authRepository),
            LoginGoogleUseCase = LoginGoogleUseCase(authRepository),
            SendRecoverPasswordEmailUseCase = SendRecoverPasswordEmailUseCase(authRepository),
            // Main
            GetUserInfoUseCase = GetUserInfoUseCase(mainRepository),
            GetTripsUseCase = GetTripsUseCase(mainRepository),
            CreateTripUseCase = CreateTripUseCase(mainRepository),
            CreateEventUseCase = CreateEventUseCase(mainRepository),
            GetEventsUseCase = GetEventsUseCase(mainRepository),
            RemoveEventUseCase = RemoveEventUseCase(mainRepository),
            RemoveTripUseCase = RemoveTripUseCase(mainRepository),
            RateTrip = RateTrip(mainRepository),
            GetPlaceImageUseCase = GetPlaceImageUseCase(mainRepository),
            GetEventPhotoUseCase = GetEventPhotoUseCase(mainRepository),
            GetTripPhotoUseCase = GetTripPhotoUseCase(mainRepository),
            UpdateEventUseCase = UpdateEventUseCase(mainRepository),
            UpdateUserInfoUseCase = UpdateUserInfoUseCase(mainRepository)
        )
    }
}