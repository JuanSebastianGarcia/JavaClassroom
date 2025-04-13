package co.uniquindio.ingesis.service.interfaces;


import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackDto;
import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackResponseDto;
import java.util.List;

public interface FeedbackServiceInterface {
    FeedbackResponseDto agregarFeedback(FeedbackDto dto);
    List<FeedbackResponseDto> obtenerFeedbackPorPrograma(Long programId);
    FeedbackResponseDto actualizarFeedback(Long feedbackId, FeedbackDto dto);
    void eliminarFeedback(Long feedbackId);

}
